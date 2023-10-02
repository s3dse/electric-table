(ns app.date-range-picker
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as h]))

(e/defn DateRangePicker [!state classes]
  (h/div (h/props {:class "flex flex-col m-2 gap-2"})
    (h/div (h/props {:class "flex gap-4"})
      (h/label
        (h/props {:for "start_date" :class "self-center w-[4rem]"})
        (h/text "Start"))
      (h/input
        (h/props {:id "start_date" :class (str "self-center border rounded " (:controls classes)) :type "date" :value (:start @!state)})
        (h/on "change" (e/fn [e] (swap! !state assoc :start (.. e -target -value))))))
    (h/div (h/props {:class "flex gap-4"})
      (h/label
        (h/props {:for "end_date" :class "self-center w-[4rem]"})
        (h/text "End"))
      (h/input
        (h/props {:id "end_date" :class (str "self-center border rounded " (:controls classes)) :type "date" :value (:end @!state)})
        (h/on "change" (e/fn [e] (swap! !state assoc :end (.. e -target -value))))))))