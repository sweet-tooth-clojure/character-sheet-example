(ns character-sheet.endpoint.character-sheet
  (:require [character-sheet.endpoint.common :as lc]
            [sweet-tooth.endpoint.utils :as eu]
            [compojure.core :refer :all]
            [character-sheet.db.query.character-sheet :as qcs]
            [character-sheet.db.query.page :as qpg]))

(defn decisions
  [component]
  (lc/initialize-decisions
    component
    {:list {:handle-ok (fn [ctx]
                         [(->> (qcs/character-sheets (lc/db ctx))
                               (qpg/paginate (lc/page-params ctx)))])}
     :show {:handle-ok #(-> (qcs/character-sheet (lc/db %) (eu/ctx-id %))
                            lc/format)}

     :update {:put! #(-> @(eu/update %) eu/add-result)
              :handle-ok #(-> (qcs/character-sheet (eu/db-after %) (eu/ctx-id %))
                              lc/format)}

     :delete {:delete! (comp deref eu/delete)}
     
     :create {:post! (fn [ctx] (eu/add-result @(eu/create (update-in ctx [:request :params] dissoc :page))))
              :handle-created (fn [ctx]
                                [(->> (qcs/character-sheets (eu/db-after ctx))
                                      (qpg/paginate (assoc (:page (eu/params ctx)) :page 1)))])}}))

(def endpoint (lc/endpoint "/api/v1/character-sheet" decisions))
