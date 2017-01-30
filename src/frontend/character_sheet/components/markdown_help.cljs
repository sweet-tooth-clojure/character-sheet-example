(ns character-sheet.components.markdown-help
  (:require [re-frame.core :refer [dispatch subscribe]]
            [sweet-tooth.frontend.core.handlers :as stch]))

(defn markdown-help
  []
  (let [show (subscribe [:key :ui :show-markdown-help])]
    (fn []
      (when @show
        [:div.markdown-help
         [:div.container
          [:table
           [:tbody
            [:tr
             [:td {:col-span 2}
              [:h3 "Markdown Cheatsheet "
               [:span.cancel
                {:on-click #(dispatch [::stch/toggle [:ui :show-markdown-help]])}
                [:i.fa.fa-times]
                " close"]]]]
            [:tr
             [:td [:em "you type:"]]
             [:td [:em "you see:"]]]
            [:tr
             [:td "# Header 1"]
             [:td [:h1 "Header 1"]]]
            [:tr
             [:td "## Header 2"]
             [:td [:h2 "Header 2"]]]
            [:tr
             [:td "### Header 3"]
             [:td [:h3 "Header 3"]]]
            [:tr
             [:td "### Header 4"]
             [:td [:h4 "Header 4"]]]
            [:tr
             [:td "*italics*"]
             [:td [:em "italics"]]]
            [:tr
             [:td "**bold**"]
             [:td [:strong "bold"]]]
            [:tr
             [:td "[google!](http://google.com)"]
             [:td [:a {:href "http://google.com"} "google!"]]]
            [:tr
             [:td "* item 1" [:br] "* item 2" [:br] "* item 3"]
             [:td [:ul
                   [:li "item 1"]
                   [:li "item 2"]
                   [:li "item 3"]]]]
            [:tr
             [:td "<email-address@gmail.com>"]
             [:td [:a {:href "mailto:email-address@gmail.com"} "email-address@gmail.com"]]]]]]]))))
