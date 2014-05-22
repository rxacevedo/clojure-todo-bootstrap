(ns webdev.item.view
  (:require [hiccup.page :refer [html5]]
            [hiccup.core :refer [html h]]))

(defn new-item []
  (html
   [:form.form-horizontal
    {:method "POST" :action "/items"}
    [:div.form-group
     [:label.control-label.col-sm-2 {:for :name-input}
      "Name"]
     [:div.col-sm-6
      [:input#name-input.form-control
       {:name :name
        :placeholder "Name"}]]]
    [:div.form-group
     [:label.control-label.col-sm-2 {:for :desc-input}
      "Description "]
     [:div.col-sm-6
      [:input#desc-input.form-control
       {:name :description
        :placeholder "Description"}]]]
    [:div.form-group
     [:div.col-sm-offset-2.col-sm-10
      [:input.btn.btn-primary
       {:type :submit
        :value "New item"}]]]]))

(defn delete-item-form [id]
  (html
   [:form.form-horizontal
    {:method "POST" :action (str "/items/" id)}
    [:input {:type :hidden
             :name "_method"
             :value "DELETE"}]
    [:div.btn-group
     [:input.btn.btn-danger.btn-xs
      {:type :submit
       :value "Delete"}]]]))

(defn check-item-form [id checked]
  (html
   [:form.form-horizontal
    {:method "POST" :action (str "/items/" id)}
    [:input {:type :hidden
             :name "_method"
             :value "PUT"}]
    [:input {:type :hidden
             :name "checked"
             :value (if checked "false" "true")}]
    [:div.btn-group
     [:button.btn.btn-primary.btn-xs
      (if checked "DONE" "TODO")]]]))

(defn items-page [items]
  (html5 {:lang :en}
         [:head
          [:title "Todo List"]
          [:meta {:name :viewport
                  :content "width=device-width, initial-scale=1.0"}]
          [:link {:href "/bootstrap/css/bootstrap.min.css"
                  :rel :stylesheet}]
          [:link {:href "/css/main.css"
                  :rel :stylesheet}]
          [:nav.navbar.navbar-default {:role "navigation"}
           [:div.container
            [:ul.nav.nav-pills.mynav
             [:li [:a {:href "/"} "Home"]]
             [:li.active [:a {:href "/items"} "Todos"]]
             [:li.dropdown
              [:a.dropdown-toggle
               {:data-toggle "dropdown"
                :href "#"}
               "Giggles"
               [:span.caret]]
              [:ul.dropdown-menu
               [:li [:a {:href "/hello"} "Hello"]]
               [:li [:a {:href "/goodbye"} "Goodbye"]]]]]]]]
         [:body
          [:div.container
           [:h1 "My Items"]
           [:div.row
            (if (seq items)
              [:table.table.table-striped
               [:thead
                [:tr
                 [:th.col-sm-2]
                 [:th.col-sm-2]
                 [:th "Name"]
                 [:th "Description"]]]
               [:tbody
                (for [i items]
                  [:tr
                   [:td (delete-item-form (:id i))]
                   [:td (check-item-form (:id i) (:checked i))]
                   [:td (h (:name i))]
                   [:td (h (:description i))]])]]
              [:div.col-sm-offset-1 "There are no items."])]
           [:div.col-sm-6
            [:h2 "Create a new item"]
            (new-item)]]
          [:script {:src "http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"}]
          [:script {:src "/bootstrap/js/bootstrap.min.js"}]]))
