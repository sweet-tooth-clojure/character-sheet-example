(ns character-sheet.subs
  (:require [re-frame.core :refer [reg-sub trim-v]]
            [sweet-tooth.frontend.core.utils :as stcu]
            [sweet-tooth.frontend.paths :as paths]
            [sweet-tooth.frontend.routes.utils :as stru]))

(defn param
  [db & path]
  (get-in db (into [:nav :params] path)))

(reg-sub :key
  (fn [db [_ & path]] (get-in db path)))

;; page
(reg-sub :pager
  (fn [db [_ query-id]]
    (let [query (get-in db [:page :query query-id])]
      {:query query
       :result (get-in db [:page :result query])})))

(reg-sub :page-data
  (fn [db [_ query-id]]
    (let [query (get-in db [:page :query query-id])
          results (get-in db [:page :result query])]
      (map #(get-in db [paths/entity-prefix (:type query) %]) (:ordered-ids results)))))

(reg-sub :page-result
  (fn [db [_ query-id]]
    (let [query (get-in db [:page :query query-id])]
      (get-in db [:page :result query]))))

(reg-sub :character-sheet
  (fn [db _]
    (stru/routed-entity db :character-sheet :character-sheet-id js/parseInt)))
