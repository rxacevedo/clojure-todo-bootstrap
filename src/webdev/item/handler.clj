(ns webdev.item.handler
  (:require [webdev.item.model :refer [create-item
                                       read-items
                                       update-item
                                       delete-item]]
            [webdev.item.view :refer [items-page]]))

(defn handle-index-items [req]
  (let [db (:webdev/db req)
        items (read-items db)]
    {:status 200
     :body (items-page items)
     :headers {}}))

(defn handle-create-item [req]
  (let [name (get-in req [:form-params "name"])
        description (get-in req [:form-params "description"])
        db (:webdev/db req)
        item-id (create-item db name description)]
    {:status 302 ;; Redirect so that a browser reload does not re-post the form
     :headers {"Location" "/items"}
     :body ""}))

(defn handle-delete-item [req]
  (let [db (:webdev/db req)
        item-id (java.util.UUID/fromString (get-in req [:route-params :item-id]))
        exists? (delete-item db item-id)]
    (if exists?
      {:status 302
       :headers {"Location" "/items"}
       :body ""}
      {:status 404
       :body "List not found"
       :headers {}})))
