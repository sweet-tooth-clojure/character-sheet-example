(ns character-sheet.utils)

(defn flatv
  [& args]
  (into [] (flatten args)))
