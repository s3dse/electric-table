(ns app.dropdown
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

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
      (dom/div (dom/props {:class (:toggle-label classes)}) (dom/text "Items per page"))
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