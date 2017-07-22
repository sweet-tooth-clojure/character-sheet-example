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
  (stpf/GET-fx #(str "/api/v1/character-sheet?" %)
               {:page 1
                :per-page 10
                :sort-by :character-sheet/name
                :sort-order :asc
                :query-id :character-sheets
                :type :character-sheet}))

(reg-event-fx :load-character-sheet
  [trim-v]
  (strf/GET-fx #(str "/api/v1/character-sheet/" (first %2))))

;; initialize the handler with no interceptors
(strf/reg-http-event-fx [])
