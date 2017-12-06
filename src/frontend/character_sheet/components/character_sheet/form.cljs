(ns character-sheet.components.character-sheet.form
  (:require [re-frame.core :as rf]
            [sweet-tooth.frontend.form.components :as stfc]
            [sweet-tooth.frontend.form.flow :as stff]
            [sweet-tooth.frontend.pagination.flow :as stpf]
            [character-sheet.components.ui :as ui]
            [clojure.string :as str]))


(defn reset-button
  [form-dirty? form-path]
  (if @form-dirty?
    [:button.submit.secondary
     {:on-click #(do (.preventDefault %)
                     (rf/dispatch [::stff/reset-form form-path]))} "reset"]))

(defn form
  [character-sheet & [page]]
  (let [id        (:db/id character-sheet)
        verb      (if id "edit" "create")
        form-path (if id
                    [:character-sheet :update id]
                    [:character-sheet :create])
        
        {:keys [form-state
                form-ui-state
                form-errors
                form-dirty?
                input]} (stfc/form form-path)

        form-spec (if id
                    {:clear :all}
                    {:success ::stpf/submit-form-success-page
                     :data    {:page page}
                     :clear   :all})]
    [:div.character-sheet-form.form-container
     [ui/form-toggle form-path (str verb " character-sheet") "hide form" character-sheet]
     [ui/vertical-slide form-ui-state
      [:div.form
       [:h2 (str verb " character sheet")]
       [:form (stfc/on-submit form-path form-spec)
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
        [:input {:type "submit" :value "submit"}]
        [reset-button form-dirty? form-path]
        [stfc/progress-indicator form-state]]]]]))

