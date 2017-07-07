(ns character-sheet.endpoint.character-sheet
  (:require [character-sheet.endpoint.common :as lc]
            [sweet-tooth.endpoint.utils :as eu]
            [sweet-tooth.endpoint.page :as epg]
            [sweet-tooth.endpoint.datomic :as ed]
            [compojure.core :refer :all]
            [character-sheet.db.query.character-sheet :as qcs]))

(defn decisions
  [component]
  (lc/initialize-decisions
    component
    {:list {:handle-ok (fn [ctx]
                         [(->> (qcs/character-sheets (lc/db ctx))
                               (epg/paginate (lc/page-params ctx)))])}
     :show {:handle-ok #(-> (qcs/character-sheet (lc/db %) (eu/ctx-id %))
                            lc/format)}

     :update {:put! #(-> @(ed/update %) (eu/->ctx :result))
              :handle-ok #(-> (qcs/character-sheet (ed/db-after %) (eu/ctx-id %))
                              lc/format)}

     :delete {:delete! (comp deref ed/delete)}
     
     :create {:post! #(-> @(ed/create (update-in % [:request :params] dissoc :page))
                          (eu/->ctx :result))
              :handle-created (fn [ctx]
                                [(->> (qcs/character-sheets (ed/db-after ctx))
                                      (epg/paginate (assoc (:page (eu/params ctx)) :page 1)))])}}))

(def endpoint (lc/endpoint "/api/v1/character-sheet" decisions))
