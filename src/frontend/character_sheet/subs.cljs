(ns character-sheet.subs
  (:require [re-frame.core :refer [reg-sub trim-v]]
            [sweet-tooth.frontend.core.utils :as stcu]
            [sweet-tooth.frontend.paths :as paths]))

(defn param
  [db & path]
  (get-in db (into [:nav :params] path)))

(reg-sub :key
  (fn [db [_ & path]] (get-in db path)))

(reg-sub :form-data
  (fn [db [_ & path]] (get-in db (stcu/flatv :forms path :data))))

(reg-sub :form-errors
  (fn [db [_ & path]] (get-in db (stcu/flatv :forms path :errors))))

(reg-sub :form-state
  (fn [db [_ & path]] (get-in db (stcu/flatv :forms path :state))))

(reg-sub :form-ui-state
  (fn [db [_ & path]] (get-in db (stcu/flatv :forms path :ui-state))))

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
    (get-in db [paths/entity-prefix :character-sheet (js/parseInt (param db :character-sheet-id))])))
