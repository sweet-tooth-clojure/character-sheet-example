(ns character-sheet.core
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch-sync dispatch subscribe]]
            [accountant.core :as acc]
            [character-sheet.dispatch]
            [character-sheet.handlers]
            [character-sheet.subs]
            [character-sheet.components.markdown-help :as mh]
            [sweet-tooth.frontend.core.utils :as stcu]))

(enable-console-print!)

;; treat node lists as seqs; not related to the rest
(extend-protocol ISeqable
  js/NodeList
  (-seq [node-list] (array-seq node-list))

  js/HTMLCollection
  (-seq [node-list] (array-seq node-list)))


(defn app
  []
  (let  [route-component (subscribe [:key :nav :routed-component])]
    (fn []
      (let [route-component @route-component]
        [:div.app
         [:div.masthead
          [:div.site-name "Ye Olde Character Sheets"]]
         route-component
         [mh/markdown-help]]))))

(defn -main []
  (r/render-component [app] (stcu/el-by-id "app"))
  (acc/dispatch-current!))

(-main)
