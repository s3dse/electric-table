(ns app.table-page
  (:require #?(:clj [next.jdbc :as jdbc])
            #?(:clj [next.jdbc.result-set :as rs])
            #?(:clj [clojure.tools.logging :as log])
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [app.date-range-picker :refer [DateRangePicker]]
            [app.table :refer [Table]]
            [app.dark-toggle :refer [DarkToggle]])
  (:import (hyperfiddle.electric Pending))
  #?(:clj (:import (java.time LocalDate))))

;; #?(:clj (defonce !table-data (atom [])))
;; (e/def rows (e/server (e/watch !table-data))) ; reactive table data
(defonce !table-keys (atom  {:event_date {:label "Date"     :sortable true  :formatter nil   :td-class "text-left"  :th-class "text-left"}
                             :product_id {:label "Product"  :sortable false :formatter nil   :td-class "text-left"  :th-class "text-left"}
                             :revenue    {:label "Revenue"  :sortable true  :formatter :euro :td-class "text-right" :th-class "text-right"}
                             :sales      {:label "Sales"    :sortable true  :formatter nil   :td-class "text-right" :th-class "text-right"}
                             :category   {:label "Category" :sortable false :formatter nil   :td-class "text-left"  :th-class "text-right"}}))
(e/def header (e/server (e/watch !table-keys)))

#_{:clj-kondo/ignore [:uninitialized-var]}
(e/def db)
#_{:clj-kondo/ignore [:uninitialized-var]}
(e/def !pg-conn)

(def pg  {:dbtype   "postgres"
          :dbname   "postgres"
          :user     "postgres"
          :password "postgres"})

#?(:clj (defonce !pgconn-old (-> {:dbtype   "postgres"
                                  :dbname   "postgres"
                                  :user     "postgres"
                                  :password "postgres"}
                               jdbc/get-datasource)))

#?(:clj (defn get-categories [conn]
          (->> {:builder-fn rs/as-unqualified-lower-maps}
            (jdbc/execute! conn ["SELECT category FROM mydb.purchases GROUP BY category"])
            (map :category))))

#?(:clj (defn get-data [conn state]
          (let [start (:start state)
                end (:end state)
                category (:category state)]
            (log/error "Start: " start ", End: " end ", Category: " category)
            (->> {:builder-fn rs/as-unqualified-lower-maps}
              (jdbc/execute! conn ["SELECT 
                                      event_date, 
                                      product_id, 
                                      revenue, 
                                      sales, 
                                      category 
                                    FROM mydb.purchases 
                                    WHERE event_date >= DATE(?) 
                                    AND event_date <= DATE(?)
                                    AND category = ?" start end category])
              (map #(assoc % :event_date (str (:event_date %))))))))

(e/defn CategorySelect [!state]
  (try
    (e/server
      (binding [db !pg-conn]
        (let [categories (e/offload #(get-categories !pg-conn))]
          (e/client
            (dom/div (dom/props {:class "flex gap-4 m-2"})
              (dom/label (dom/props {:for "category-select"}) (dom/text "Category"))
              (dom/select (dom/props {:id "category-select" :class "border rounded dark:bg-moon-800 dark:border-moon-700"})
                (e/for [category categories]
                  (dom/option
                    (dom/props (conj {:value category} (when (= category (:category @!state)) {:selected true})))
                    (dom/text category)))
                (dom/on "change" (e/fn [e] (swap! !state assoc :category (.. e -target -value))))))))))
    (catch Pending _
      (dom/div (dom/props {:class "bg-amber-500"}) (dom/text "LOADING...")))))

(e/defn Dropdown [!items !selected classes]
  (let [items (e/watch !items)
        selected (e/watch !selected)
        !show (atom false)
        show (e/watch !show)]
    (dom/div (dom/props {:class (if (:container classes) (:container classes) "relative flex gap-1")})
      (dom/div
        (dom/on "click" (e/fn [_] (reset! !show (not @!show))))
        (dom/text (str selected))
        (dom/props {:class (:toggle classes)})
        (dom/span (dom/props {:class "i-mdi-chevron-down flex"})))
      (dom/div (dom/text "Items per page"))
      (when show
        (dom/div (dom/props {:class (:dropdown-container classes)})
          (dom/ul (dom/props {:class (:dropdown-ul classes)})
            (e/for [item items]
              (dom/li
                (dom/props {:class (:dropdown-li classes)})
                (dom/text (str item))
                (dom/on "click" (e/fn [_]
                                  (reset! !selected item)
                                  (reset! !show (not @!show))))))))))))

(e/defn Page []
  (try
    (e/server
      (binding [!pg-conn (jdbc/get-datasource pg)]
        (let [now (str (LocalDate/now))
              tenDaysBefore (str (.minusDays (LocalDate/now) 10))
              !table-data (atom [])
              rows (e/watch !table-data)]
          (e/client
            (let [!filters (atom {:category "Finance" :start tenDaysBefore :end now})]
              (dom/div (dom/props {:class "flex justify-end p-5"})
                (DarkToggle. {:true "i-mdi-white-balance-sunny" :false "i-mdi-moon-waning-crescent" :common "flex text-xl dark:text-gray-400 text-gray-600 "}))
              (dom/div (dom/props {:class "px-4 pt-4 pb-12 text-8xl text-gray-950 dark:text-gray-300"}) (dom/text "Trendentastic Things"))
              (dom/div (dom/props {:class "flex no-wrap"})
                (dom/form (dom/props {:class "h-fit w-[26rem] max-w-[26rem] m-4 p-4 border rounded bg-gray-50 dark:bg-moon-800 dark:text-gray-300 dark:border-moon-700"})
                  (CategorySelect. !filters)
                  (DateRangePicker. !filters {:controls "dark:bg-moon-900 dark:border-moon-700"})
                  (dom/div
                    (dom/text "Confirm")
                    (dom/props {:class "w-fit h-fit p-2 mt-8 mb-4 ms-auto rounded bg-navy-500 hover:bg-navy-400 dark:hover:bg-navy-600 hover:cursor-pointer text-gray-100 active:text-navy-300"})
                    (dom/on "click"
                      (e/fn [_]
                        (let [params @!filters]
                          (try
                            (e/server
                              (reset! !table-data (e/offload #(get-data !pg-conn params))))
                            (catch Pending _)))))))
                (dom/div (dom/props {:class "border rounded m-4 dark:border-moon-700 dark:bg-moon-800 bg-gray-50"})
                  (let [!per-page (atom 5)
                        per-page (e/watch !per-page)]
                    (dom/div (dom/props {:class "w-full border-b dark:border-moon-700 my-3 text-slate-600 text-lg font-semibold"})
                      (dom/div (dom/text "Some Data") (dom/props {:class "mx-4 pb-3 dark:text-gray-300"})))

                    (Table. rows header !per-page true {:table "dark:text-gray-300 border-t"
                                                        :thead "bg-slate-100 dark:bg-moon-900 uppercase font-semibold text-[0.625rem] text-slate-500"
                                                        :th "first:ps-6 last:pe-6 p-2"
                                                        :tr "border-y dark:border-moon-700"
                                                        :td "first:ps-6 last:pe-6 p-2"} {:controls [(e/fn [] (Dropdown.
                                                                                                               (atom [5 10 15 25 50])
                                                                                                               !per-page
                                                                                                               {:toggle "flex items-center border bg-gray-100 dark:bg-moon-800 dark:border-moon-700 rounded-sm px-2 hover:cursor-pointer"
                                                                                                                :dropdown-container "absolute top-[100%] min-w-fit w-12 bg-white dark:bg-moon-800 divide-y divide-gray-100 dark:divide-moon-700 shadow dark:shadow-lg border dark:border-moon-700 rounded-sm z-2"
                                                                                                                :dropdown-ul "text-sm text-gray-700 dark:text-gray-200"
                                                                                                                :dropdown-li "hover:cursor-pointer hover:bg-opactiy-50 text-center hover:bg-gray-100 dark:hover:bg-moon-700"}))]})))))))))
    (catch Pending _
      (dom/props {:class "bg-amber"}))))
