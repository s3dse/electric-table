(ns app.table
  (:require
   [hyperfiddle.electric :as e]
   [hyperfiddle.electric-dom2 :as h]
   [hyperfiddle.electric-ui4 :as ui]
   [clojure.string :as str]))

(e/def formatters {:euro #?(:cljs (e/client
                                    (fn [v]
                                      (-> (/ v 100)
                                        (.toLocaleString "de-DE" (js-obj "style" "currency" "currency" "EUR")))))
                            :clj identity)})

(defn find-data [needle haystack]
  (filter (fn [row] (if (empty? row)
                      true
                      (let [hay (->> (vals row) (str/join " ") str/lower-case)]
                        (str/includes? hay needle))))
    haystack))

(e/defn Table [data fields !per-page search? classes &slots]
  (let [!search (atom "")
        search (e/watch !search)
        per-page (e/watch !per-page)
        !items (atom (if (empty? search) (take per-page data) (take per-page (find-data search data))))
        items (e/watch !items)
        !sort-order (atom {:key nil :direction nil})
        sort-order (e/watch !sort-order)]
    (h/div (h/props {:class "flex flex-wrap mx-4 my-3 gap-2 justify-start dark:text-gray-300"})
      (when search?
        (ui/input search (e/fn [v] (reset! !search v))
          (h/props {:type "search" :placeholder "Type to search"
                    :class "border dark:border-moon-700 dark:bg-moon-900 rounded-sm px-2 text-sm"})))
      (when (:controls &slots)
        (e/for [control-fn (:controls &slots)]
          (new control-fn))))

    (h/table (h/props {:class (:table classes)})
      (h/thead (h/props {:class (:thead classes)})
        (e/for [key (keys fields)]
          (let [sortable (-> fields key :sortable)
                sort-reverse? (= key (:key sort-order))
                th-class (str (when sortable "hover:cursor-pointer ") " " (:th-class (key fields)) " " (:th classes))]
            (println key)
            (h/th
              (h/props {:class th-class})
              (when sortable
                (h/on "click" (e/fn [_]
                                (let [asc? (= "asc" (:direction sort-order))]
                                  (reset! !items (sort-by key (if (and sort-reverse? asc?) > <) items))
                                  (reset! !sort-order (assoc {} :key key :direction (if (and asc? sort-reverse?)
                                                                                      "desc"
                                                                                      "asc")))))))
              (h/div (h/props {:class "inline-flex no-wrap gap-1 items-center"})
                (h/span (h/text (str (get-in fields [key :label] (name key)))))
                (h/span (h/props {:class (cond
                                           (not sortable) ""
                                           (and sort-reverse? (= "asc" (:direction sort-order))) "flex i-mdi-chevron-up"
                                           (and sort-reverse? (= "desc" (:direction sort-order))) "flex i-mdi-chevron-down"
                                           :else "flex i-mdi-sort")})))))))
      (h/tbody
        (e/for [row items]
          (h/tr (h/props {:class (:tr classes)})
            (e/for [[k v] row]
              (let [fmt-key (:formatter (k fields))
                    fmt (if fmt-key (fmt-key formatters) identity)
                    td-class (str (:td classes) " " (:td-class (k fields)))]
                (h/td
                  (h/text (fmt v))
                  (h/props {:class td-class}))))))))))
