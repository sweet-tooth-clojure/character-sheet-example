(ns character-sheet.routes
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [re-frame.core :refer [dispatch]]
            [accountant.accountant :as acc]

            [character-sheet.components.character-sheet.list :as csl]
            [character-sheet.components.character-sheet.show :as css]
            
            [sweet-tooth.frontend.core.handlers :as stch]
            [sweet-tooth.frontend.core.utils :as stcu]))

(defn page-params
  [params]
  (let [page (select-keys params [:sort-by :sort-order :page :per-page])]
    (cond-> page
      (:page page) (assoc :page (js/parseInt (:page page)))
      (:per-page page) (assoc :per-page (js/parseInt (:per-page page))))))

(defn load
  [component params page-id]
  (dispatch [::stch/assoc-in [:nav] {:routed-component component
                                     :page-id page-id
                                     :params params}]))


(defroute "/" {:as params}
  (dispatch [:load-character-sheets (page-params (:query-params params))])
  (load [csl/component] nil :home))

(defroute "/character-sheet/:character-sheet-id" {:as params}
  (dispatch [:load-character-sheet (stcu/id-num (:character-sheet-id params))])
  (load [css/component] params :show-character-sheet))

(acc/configure-navigation!)
