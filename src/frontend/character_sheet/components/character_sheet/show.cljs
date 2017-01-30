(ns character-sheet.components.character-sheet.show
  (:require [re-frame.core :refer [subscribe]]
            [character-sheet.components.character-sheet.form :as form]
            [character-sheet.components.ui :as ui]))

(defn component
  []
  (let [cs (subscribe [:character-sheet])]
    (fn []
      (let [cs @cs]
        [:div.character-sheet.container
         [:h1 (:character-sheet/name cs)]
         [form/form cs]
         [:div "story:"
          [:div (ui/markdown (:character-sheet/story cs))]]
         (map (fn [attr] ^{:key attr} [:div (name attr) ": " (attr cs)])
              [:character-sheet/level
               :character-sheet/strength
               :character-sheet/motivation
               :character-sheet/attention-span
               :character-sheet/willpower
               :character-sheet/cunning
               :character-sheet/endurance
               :character-sheet/imagination])]))))
