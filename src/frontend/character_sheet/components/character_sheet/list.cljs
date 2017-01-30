(ns character-sheet.components.character-sheet.list
  (:require [re-frame.core :refer [subscribe]]
            [character-sheet.components.paging :as p]
            [character-sheet.components.character-sheet.form :as form]))

(defn character-sheet
  [c]
  [:div.character-sheet-listing
   [:div.name
    [:a {:href (str "/character-sheet/" (:db/id c))} (:character-sheet/name c)]]])

(defn component
  []
  (let [query-id :character-sheets
        character-sheets (subscribe [:page-data query-id])
        page-state (subscribe [:key :page :state query-id])
        page-query (subscribe [:key :page :query query-id])]
    (fn []
      (let [character-sheets @character-sheets]
        [:div.character-sheets.container
         [:h1 "All Character Sheets"]
         [form/form {} @page-query]
         [p/page-nav query-id]
         (cond (= :loading @page-state) [:div {:style #js{:position "relative"}} [:div.zeload]]
               (empty? character-sheets) [:div "There are no character sheets."]
               :else  (map (fn [c] ^{:key (:db/id c)} [character-sheet c]) character-sheets))]))))
