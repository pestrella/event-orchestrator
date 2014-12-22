;; Author: Paolo Estrella
(ns noble.orchestrator
  (import [com.thisisnoble.javatest Orchestrator]
          [com.thisisnoble.javatest.impl CompositeEvent]))

(def processors (atom []))
(def publisher (atom nil))
(def events (atom {}))

(defn debug [event]
  (println (str "Recieved event: ("
                (.getId event)
                (when-let [parent-id (.getParentId event)]
                  (str "[" parent-id "]"))
                ")")))

(defn get-root [events event]
  (loop [parent (events (.getParentId event))
         event nil]
    (if parent
      (recur (.getParent parent) parent)
      event)))

(defn collate [events event]
  (let [parent (get-root events event)]
    (assoc (if parent
             (assoc events
               (.getId parent)
               (doto parent
                 (.addChild event)))
             events)
      (.getId event)
      (CompositeEvent. event parent))))

(def orchestrator
  (reify Orchestrator
    (register [this processor]
      (swap! processors conj processor))
    (receive [this event]
      (do
        (debug event)
        (swap! events collate event)
        (.publish @publisher (get-root @events event))
        (doseq [processor @processors]
          (when (.interestedIn processor event)
            (.process processor event)))))
    (setup [this pub]
      (reset! publisher pub))))

(import '[com.thisisnoble.javatest SimpleOrchestratorTest])
(doto (SimpleOrchestratorTest. )
  (.setOrchestrator orchestrator)
  (.tradeEventShouldTriggerAllProcessors))
(doto (SimpleOrchestratorTest. )
  (.setOrchestrator orchestrator)
  (.shippingEventShouldTriggerOnly2Processors))
