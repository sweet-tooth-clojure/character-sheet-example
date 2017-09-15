(ns character-sheet.dispatch
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [re-frame.core :refer [dispatch]]
            [accountant.core :as acc]
            [bidi.bidi :as bidi]
            [clojure.string :as str]

            [character-sheet.routes :as routes]
            [character-sheet.components.character-sheet.list :as csl]
            [character-sheet.components.character-sheet.show :as css]
            
            [sweet-tooth.frontend.core.utils :as stcu]
            [sweet-tooth.frontend.routes.flow :as strf]
            [sweet-tooth.frontend.routes.utils :as stru]))

(defmulti dispatch-route (fn [handler params] handler))

(defmethod dispatch-route
  :home
  [handler params]
  (dispatch [:load-character-sheets (stru/page-params params)])
  (dispatch [::strf/load :home [csl/component] params]))

(defmethod dispatch-route
  :show-character-sheet
  [handler params]
  (dispatch [:load-character-sheet (stcu/id-num (:character-sheet-id params))])
  (dispatch [::strf/load :show-character-sheet [css/component] params]))

(defonce nav
  ;; Prevent this from getting re-configured on every change
  (acc/configure-navigation!
    {:nav-handler
     (fn [path]
       (let [match (bidi/match-route routes/routes path)]
         (dispatch-route (:handler match)
                         (merge (:route-params match)
                                (stru/query-params path)))))
     :path-exists?
     (fn [path]
       (boolean (bidi/match-route routes/routes path)))}))
