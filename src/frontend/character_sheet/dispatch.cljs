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
            [sweet-tooth.frontend.routes :as stro]))

(defmulti dispatch-route (fn [handler params] handler))

(defmethod dispatch-route
  :home
  [handler params]
  (dispatch [:load-character-sheets (stro/page-params params)])
  (stro/load [csl/component] params :home))

(defmethod dispatch-route
  :show-character-sheet
  [handler params]
  (dispatch [:load-character-sheet (stcu/id-num (:character-sheet-id params))])
  (stro/load [css/component] params :show-character-sheet))


(defonce nav
  ;; Prevent this from getting re-configured on every change
  (acc/configure-navigation!
    {:nav-handler
     (fn [path]
       (let [match (bidi/match-route routes/routes path)]
         (dispatch-route (:handler match) (merge (:route-params match) (stro/query-params path)))))
     :path-exists?
     (fn [path]
       (boolean (bidi/match-route routes/routes path)))}))
