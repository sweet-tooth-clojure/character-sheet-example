(ns character-sheet.components.ui.animate-height
  (:require [cljsjs.marked]
            [reagent.core :refer [atom] :as r]
            [clojure.string :as str]))

(defn animate-css-height-mixin
  [label]
  (let [tick 17
        label (str "#" label)]
    {:component-will-mount
     (fn []
       (let [style (doto (js/document.createElement "style")
                     (.appendChild (js/document.createTextNode "")))
             _     (js/document.head.appendChild style)     
             sheet (.-sheet style)]
         (.insertRule sheet
                      (str label ".anim-enter, "
                           label ".anim-leave.anim-leave-active { height: 0; margin: 0; padding: 0;}")
                      0)
         (this-as this
           (aset this "style" style)
           (aset this "sheet" sheet))))

     :component-will-enter
     (fn [done]
       (this-as this
         (let [node (js/ReactDOM.findDOMNode this)
               computed-style (js/window.getComputedStyle node nil)
               directions ["Top" "Right" "Bottom" "Left"]
               margin (->> directions
                           (map #(aget computed-style (str "margin" %)))
                           (str/join " "))
               padding (->> directions
                            (map #(aget computed-style (str "padding" %)))
                            (str/join " "))
               sheet (.-sheet this)]
           (.insertRule sheet
                        (str label ".anim-enter.anim-enter-active { height: " (.-height computed-style) "; "
                             "margin: " margin "; padding: " padding "; }")
                        1)
           (.insertRule sheet (str label "{ display: none; }") 2)

           (js/setTimeout (fn []
                            (.add (.-classList node) "anim-enter")
                            (.deleteRule sheet 2)
                            (js/setTimeout (fn []
                                             (-> node .-classList (.add "anim-enter-active"))                                             
                                             (js/setTimeout done 350))
                                           tick))))))

     :component-did-enter
     (fn []
       (this-as this
         (let [node (js/ReactDOM.findDOMNode this)]
           (-> node .-classList (.remove "anim-enter"))
           (-> node .-classList (.remove "anim-enter-active")))))
     

     :component-will-leave
     (fn [done]
       (this-as this
         (let [node (js/ReactDOM.findDOMNode this)
               client-height (.-clientHeight node)]
           (-> this .-sheet (.insertRule (str label ".anim-leave { height: " client-height "px; }") 1))
           (-> node .-classList (.add "anim-leave"))

           (js/setTimeout (fn []
                            (-> node .-classList (.add "anim-leave-active"))
                            (js/setTimeout done 350))
                          tick))))

     :component-will-unmount
     (fn [] (this-as this (js/document.head.removeChild (.-style this))))}))

(defn height-class
  [id]
  (r/create-class (merge (animate-css-height-mixin id)
                         {:display-name "item"
                          :reagent-render (fn [child] [:div {:id id} child])})))
