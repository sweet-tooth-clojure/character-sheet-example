(ns character-sheet.handlers
  (:require [ajax.core :refer [GET]]
            [re-frame.core :refer [reg-event-fx trim-v]]
            [secretary.core :as secretary]
            [sweet-tooth.frontend.core.handlers :as stch]
            [sweet-tooth.frontend.form.handlers :as stfh]
            [sweet-tooth.frontend.pagination.handlers :as stph]
            [sweet-tooth.frontend.remote.handlers :as strh]))

(defmethod stfh/url-prefix :default [_ _] "/api/v1")
(defmethod stfh/data-id :default [_ _ data] (:db/id data))

(reg-event-fx :load-character-sheets
  [trim-v]
  (fn [{:keys [db]} [page-params]]
    (let [query-id :character-sheets
          page-query (merge {:page 1
                             :per-page 10
                             :sort-by :character-sheet/name
                             :sort-order :asc
                             :query-id query-id
                             :type :character-sheet}
                            page-params)]
      {::strh/http {:method GET
                    :url (str "/api/v1/character-sheet?" (secretary/encode-query-params page-query))
                    :on-success [::stph/merge-page]}
       :db (-> db
               (assoc-in [:page :query query-id] page-query)
               (assoc-in [:page :state query-id] :loading))})))


(reg-event-fx :load-character-sheet
  [trim-v]
  (fn [{:keys [db]} [id]]
    {::strh/http {:method GET
                  :url (str "/api/v1/character-sheet/" id)
                  :on-success [::stch/deep-merge]}
     :db db}))

;; initialize the handler with no interceptors
(strh/reg-http-event-fx [])
