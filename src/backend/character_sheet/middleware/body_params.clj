(ns character-sheet.middleware.body-params)

(defn wrap-body-params
  [f]
  (fn [req]
    (f (if-let [bp (:body-params req)]
         (assoc req :params bp)
         req))))
