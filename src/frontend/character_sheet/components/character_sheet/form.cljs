(ns character-sheet.components.character-sheet.form
  (:require [sweet-tooth.frontend.form.components :as stfc]
            [sweet-tooth.frontend.pagination.handlers :as stph]
            [sweet-tooth.frontend.form.handlers :as stfh]
            [character-sheet.components.ui :as ui]
            [clojure.string :as str]))

(defn form
  [c & [page]]
  (let [id (:db/id c)
        verb (if id "edit" "create")
        form-path (if id
                    [:character-sheet :update id]
                    [:character-sheet :create])
        {:keys [form-state ui-state input]} (stfc/form form-path)]
    [:div.character-sheet-form.form-container
     [ui/form-toggle form-path (str verb " character-sheet") "hide form" c]
     [ui/vertical-slide ui-state
      [:div.form
       [:h2 (str verb " character sheet")]
       [:form (stfc/on-submit form-path (cond-> {:success (if id ::stfh/clear-on-success ::stph/clear-on-success-page)}
                                          page (assoc :data {:page page}) ))
        [input :text :character-sheet/name]
        [input :number :character-sheet/level]
        [input :textarea :character-sheet/story]
        [input :number :character-sheet/strength]
        [input :number :character-sheet/motivation]
        [input :number :character-sheet/attention-span]
        [input :number :character-sheet/willpower]
        [input :number :character-sheet/cunning]
        [input :number :character-sheet/endurance]
        [input :number :character-sheet/imagination]
        [:input {:type "submit" :value verb}]
        [stfc/progress-indicator form-state]
        [stfc/form-errors form-path]]]]]))

