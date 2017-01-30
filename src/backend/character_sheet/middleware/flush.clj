(ns character-sheet.middleware.flush)

(defn wrap-flush
  [f]
  (fn [req]
    (let [res (f req)]
      (flush)
      res)))
