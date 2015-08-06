(ns todo.view
  (:require [hiccup.core :refer [h html]]
            [hiccup.page :refer [html5]]))

(defn index-page []
  (html5 {:lang :en}
         [:head
          [:title "Clojure Webdev"]
          [:meta {:name :viewport
                  :content "width=device-width, initial-scale=1.0"}]
          [:link {:href "/bootstrap/css/bootstrap.min.css"
                  :rel :stylesheet}]
          [:link {:href "/css/main.css"
                  :rel :stylesheet}]
          [:nav.navbar.navbar-default {:role "navigation"}
           [:div.container
            [:ul.nav.nav-pills.mynav
             [:li.active [:a {:href "/"} "Home"]]
             [:li [:a {:href "/items"} "Todos"]]
             [:li.dropdown
              [:a.dropdown-toggle {:data-toggle "dropdown"
                                   :href "#"}
               "Giggles"
               [:span.caret]]
              [:ul.dropdown-menu
               [:li [:a {:href "/hello"} "Hello"]]
               [:li [:a {:href "/goodbye"} "Goodbye"]]]]]]]]
         [:div.container
          [:body
           [:header
            [:h1 "Hi there, sailor!"]]
           [:footer
            [:script {:src "http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"}]
            [:script {:src "/bootstrap/js/bootstrap.min.js"}]]]]))
