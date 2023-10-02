(ns app.counter
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as h]
            #?(:clj [next.jdbc :as jdbc])
            #?(:clj [next.jdbc.result-set :as rs]))
  (:import (hyperfiddle.electric Pending)))

(e/def db)

#?(:clj (defonce !pgconn (-> {:dbtype   "postgres"
                              :dbname   "postgres"
                              :user     "postgres"
                              :password "postgres"}
                           jdbc/get-datasource
                           jdbc/get-connection)))

#?(:clj (defn get-counter [conn]
          (->> {:builder-fn rs/as-unqualified-lower-maps} 
            (jdbc/execute! conn ["SELECT value FROM mydb.Number WHERE id = 1"])
            first
            :value)))

#?(:clj (defn update-counter [conn value]
          (jdbc/execute! conn ["UPDATE mydb.Number SET value = ? WHERE id = 1" value])))

(e/defn Counter []
  (try
    (e/server
      (binding [db !pgconn]
        (let [counter (e/offload #(get-counter !pgconn))]
          (e/client
            (let [!state (atom counter)]
              (h/div (h/props {:class "p-12"})
                (h/div (h/text "Counter") (h/props {:class "text-2xl font-semibold pb-3"}))
                (h/div (h/props {:class "flex justify-end border rounded w-fit min-w-[8rem] h-fit my-2 p-2"})
                  (h/p (h/text (e/watch !state)) (h/props {:class "self-center w-fit p-3 font-bold font-mono"}))
                  (h/div (h/props {:class "flex-col py-1"})
                    (h/div
                      (h/on "click" (e/fn [_] (let [v (swap! !state inc)]
                                                  (e/server (e/offload #(update-counter !pgconn v))))))
                      (h/props {:class "w-fit h-fit m-1 border rounded hover:bg-gray-100 bg-gray-200 group"})
                      (h/span (h/props {:class "block i-mdi-plus group-active:bg-gray-500"})))
                    (h/div
                      (h/on "click" (e/fn [_] (let [v (swap! !state dec)]
                                                  (e/server (e/offload #(update-counter !pgconn v))))))
                      (h/props {:class "w-fit h-fit m-1 border rounded hover:bg-gray-100 bg-gray-200 group"})
                      (h/span (h/props {:class "block i-mdi-minus group-active:bg-gray-500"})))))))))))
    (catch Pending _
      (h/div (h/props {:class "bg-amber-500"}) "LOADING..."))))