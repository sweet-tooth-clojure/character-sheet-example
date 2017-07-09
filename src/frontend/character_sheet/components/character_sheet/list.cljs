(ns character-sheet.components.character-sheet.list
  (:require [re-frame.core :refer [subscribe]]
            [sweet-tooth.frontend.pagination.flow :as stpf]
            [character-sheet.routes :as routes]
            [character-sheet.components.paging :as p]
            [character-sheet.components.character-sheet.form :as form]
            [bidi.bidi :as bidi]))

(defn character-sheet
  [c]
  [:div.character-sheet-listing
   [:div.name
    [:a {:href (bidi/path-for routes/routes :show-character-sheet :character-sheet-id (:db/id c))}
     (:character-sheet/name c)]]])

(defn component
  []
  (let [query-id :character-sheets
        character-sheets (subscribe [::stpf/page-data query-id])
        page-state (subscribe [::stpf/page-state query-id])
        page-query (subscribe [::stpf/page-query query-id])]
    (fn []
      (let [character-sheets @character-sheets]
        [:div.character-sheets.container
         [:h1 "All Character Sheets"]
         [form/form {} @page-query]
         [p/page-nav query-id]
         (cond (= :loading @page-state) [:div {:style #js{:position "relative"}} [:div.zeload]]
               (empty? character-sheets) [:div "There are no character sheets."]
               :else  (map (fn [c] ^{:key (:db/id c)} [character-sheet c]) character-sheets))]))))
