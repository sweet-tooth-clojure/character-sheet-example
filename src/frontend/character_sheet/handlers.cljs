(ns character-sheet.handlers
  (:require [ajax.core :refer [GET]]
            [re-frame.core :refer [reg-event-fx trim-v]]
            [secretary.core :as secretary]
            [sweet-tooth.frontend.core.flow :as stcf]
            [sweet-tooth.frontend.form.flow :as stff]
            [sweet-tooth.frontend.remote.flow :as strf]
            [sweet-tooth.frontend.pagination.flow :as stpf]))

(defmethod stff/url-prefix :default [_ _] "/api/v1")
(defmethod stff/data-id :default [_ _ data] (:db/id data))

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
      {::strf/http {:method GET
                    :url (str "/api/v1/character-sheet?" (secretary/encode-query-params page-query))
                    :on-success [::stpf/merge-page]}
       :db (stpf/update-db-page-loading db page-query query-id)})))


(reg-event-fx :load-character-sheet
  [trim-v]
  (fn [{:keys [db]} [id]]
    {::strf/http {:method GET
                  :url (str "/api/v1/character-sheet/" id)
                  :on-success [::stcf/deep-merge]}
     :db db}))

;; initialize the handler with no interceptors
(strf/reg-http-event-fx [])
