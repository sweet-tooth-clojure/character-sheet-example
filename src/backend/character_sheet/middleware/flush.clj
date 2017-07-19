(ns character-sheet.middleware.flush)

(defn wrap-flush
  "Flush output after each request"
  [f]
  (fn [req]
    (let [res (f req)]
      (flush)
      res)))
