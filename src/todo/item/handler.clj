(ns todo.item.handler
  (:require [todo.item.model :refer [create-item
                                     read-items
                                     update-item
                                     delete-item]]
            [todo.item.view :refer [items-page]]))

(defn handle-index-items [req]
  (let [db (:todo/db req)
        items (read-items db)]
    {:status 200
     :body (items-page items)
     :headers {}}))

(defn handle-create-item [req]
  (let [name (get-in req [:form-params "name"])
        description (get-in req [:form-params "description"])
        db (:todo/db req)
        item-id (create-item db name description)]
    {:status 302 ;; Redirect so that a browser reload does not re-post the form
     :headers {"Location" "/items"}
     :body ""}))

(defn handle-update-item [req]
  (let [db (:todo/db req)
        item-id (java.util.UUID/fromString (get-in req [:route-params :item-id]))
        checked (get-in req [:params "checked"])
        exists? (update-item db item-id (= "true" checked))]
    (if exists?
      {:status 302
       :headers {"Location" "/items"}
       :body ""}
      {:status 500
       :body "An error occured while updating the item's status."
       :headers {}})))

(defn handle-delete-item [req]
  (let [db (:todo/db req)
        item-id (java.util.UUID/fromString (get-in req [:route-params :item-id]))
        exists? (delete-item db item-id)]
    (if exists?
      {:status 302
       :headers {"Location" "/items"}
       :body ""}
      {:status 404
       :body "List not found"
       :headers {}})))
