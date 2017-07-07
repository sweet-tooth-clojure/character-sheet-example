(ns character-sheet.routes
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [re-frame.core :refer [dispatch]]
            [accountant.core :as acc]
            [bidi.bidi :as bidi]

            [character-sheet.components.character-sheet.list :as csl]
            [character-sheet.components.character-sheet.show :as css]
            
            [sweet-tooth.frontend.core.handlers :as stch]
            [sweet-tooth.frontend.core.utils :as stcu]
            [sweet-tooth.frontend.routes :as stro]))

(def routes ["/" {"" :home
                  ["character-sheet/" :character-sheet-id] :show-character-sheet}])

(defroute "/" {:as params}
  (dispatch [:load-character-sheets (stro/page-params (:query-params params))])
  (stro/load [csl/component] nil :home))

(defroute "/character-sheet/:character-sheet-id" {:as params}
  (dispatch [:load-character-sheet (stcu/id-num (:character-sheet-id params))])
  (stro/load [css/component] params :show-character-sheet))

(defonce nav
  ;; Prevent this from getting re-configured on every change
  (acc/configure-navigation!
    {:nav-handler
     (fn [path]
       (pr "BIDI" path (bidi/match-route routes path))
       (secretary/dispatch! path))
     :path-exists? secretary/locate-route}))
