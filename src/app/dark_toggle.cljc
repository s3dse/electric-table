(ns app.dark-toggle
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as h]))

(e/defn DarkToggle [toggle-classes]
  (let [make-doc-dark! #?(:cljs #(.. js/document
                                   (getElementsByTagName "html")
                                   (item 0)
                                   (-classList)
                                   (toggle "dark"))
                          :clj identity)
        system-dark #?(:cljs (.. js/window
                               (matchMedia "(prefers-color-scheme: dark")
                               (-matches))
                       :clj nil)
        doc-dark #?(:cljs (.. js/document
                            (getElementsByTagName "html")
                            (item 0)
                            (-classList)
                            (contains "dark"))
                    :clj nil)
        !state (atom system-dark)
        state (e/watch !state)]
    (when (not= doc-dark system-dark)
      (make-doc-dark!))
    (h/div
      (h/on "click" #?(:cljs (e/fn [_]
                               (reset! !state (make-doc-dark!))
                               (println "dark: " state))
                       :clj nil))
      (h/props {:class (str (:common toggle-classes) ((keyword (str state)) toggle-classes))}))))