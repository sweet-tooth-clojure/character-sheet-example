(ns character-sheet.db.query.page
  (:require [clojure.spec :as s]
            [character-sheet.db.query.utils :as qu]))

(defn slice
  [page per-page ents]
  (->> ents
       (drop (* (dec page) per-page))
       (take per-page)))

(defn sort-fn
  [data sort-order]
  (if (instance? java.util.Date data)
    (if (= sort-order :desc) #(.after % %2) #(.before % %2))
    (if (= sort-order :desc) #(compare %2 %1) compare)))

(defn paginate
  [p ents]
  (let [{:keys [page per-page sort-order type]} p
        ent-count (count ents)
        data (cond->> ents
               (:sort-by p) (sort-by (:sort-by p) (sort-fn ((:sort-by p) (first ents)) sort-order))
               true (slice page per-page))]
    {:data {type (qu/key-by :db/id data)}
     :page {:query {(:query-id p) p}
            :result {p {:total-pages (Math/round (Math/ceil (/ ent-count per-page)))
                        :ent-count ent-count
                        :ordered-ids (map :db/id data)}}}}))

(defn page-to-new
  [new-ent-id page ents]
  (let [ent-page (paginate page ents)]
    (if (get-in ent-page [:data (:type page) new-ent-id])
      ent-page
      (let [[[query-id page-query]] (vec (-> ent-page :page :query))]
        (paginate (assoc page-query :page (get-in ent-page [:page :result page-query :total-pages])) ents)))))

(defn organize-page-data
  [ent-type page]
  (update-in page [:data ent-type] #(qu/key-by :db/id %)))
