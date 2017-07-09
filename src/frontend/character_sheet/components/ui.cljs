(ns character-sheet.components.ui
  (:require [cljsjs.marked]
            [reagent.core :refer [atom] :as r]
            [re-frame.core :refer [dispatch subscribe]]
            [clojure.string :as s]
            [character-sheet.components.ui.animate-height :as ah]
            [character-sheet.utils :as u]
            [sweet-tooth.frontend.core.utils :as stcu]
            [goog.events.KeyCodes :as KeyCodes]
            [sweet-tooth.frontend.paths :as paths]
            [sweet-tooth.frontend.core.flow :as stcf]))

;; markdown
(defn markdown [txt]
  {:dangerouslySetInnerHTML #js {:__html (js/marked (or txt ""))}})

(defn markdown-toggle
  []
  [:span.markdown-toggle
   {:on-click #(dispatch [::stcf/toggle [:ui :show-markdown-help]])}
   "(markdown " [:span.fa.fa-question-circle] ")"])

;; common components
(defn toggle-btn
  ([visible show-text hide-text]
   (toggle-btn visible show-text hide-text #(swap! visible not)))
  ([visible show-text hide-text on-click]
   (let [vis @visible
         classname (if vis "hide" "show")
         text      (str " " (if vis hide-text show-text))
         i-class   (if vis "fa-minus-circle" "fa-plus-circle")]
     [:div.toggle-btn
      [:span {:class classname
              :on-click on-click}
       [:i {:class (str "fa " i-class)}]
       text]])))

(defn form-toggle
  [form-path show-text hide-text & [data]]
  (let [full-form-path (paths/full-form-path form-path)
        ui-state-path (conj full-form-path :ui-state)
        visible (subscribe (u/flatv :key ui-state-path))
        toggle-fn #(dispatch [::stcf/toggle ui-state-path])]
    (toggle-btn visible
                show-text
                hide-text
                (if data
                  #(do (dispatch [::stcf/assoc-in (u/flatv full-form-path :data) data])
                       (toggle-fn))
                  toggle-fn))))

(defn focus-child
  [component tag-name & [timeout]]
  (with-meta (fn [] component)
    {:component-did-mount
     (fn [el]
       (let [node (first (.getElementsByTagName (r/dom-node el) tag-name))]
         (if timeout
           (js/setTimeout #(.focus node) timeout)
           (.focus node))))}))

(defn on-esc
  [f]
  (fn [e]
    (when (= KeyCodes/ESC (.-keyCode e))
      (f))))

;; transition group
(def rtg (r/adapt-react-class (-> js/React (aget "addons" "TransitionGroup"))))
(def ctg (r/adapt-react-class (-> js/React (aget "addons" "CSSTransitionGroup"))))

(defn vertical-slide
  [show component]
  [rtg (when @show [(ah/height-class (gensym)) component])])

;; TODO there must be a nicer way to do this where you modify the
;; component directly?
(defn require-authentication
  ""
  [component msg]
  (let [current-user (subscribe [:key :current-user])]
    (fn [component msg]
      (if @current-user
        component
        [:span.requires-authentication
         {:on-click-capture (fn [e]
                              (dispatch [:requires-authentication msg])
                              (.stopPropagation e))}
         component]))))


(defn delete-btn
  [dispatch-v]
  [:div.delete-btn
   {:on-click (fn [e]
                (.stopPropagation e)
                (dispatch dispatch-v))}
   [:i {:class "fa fa-trash"}]])
 
