# Larch (Alpha)

![The Larch](https://s-media-cache-ak0.pinimg.com/originals/17/ff/7f/17ff7f207250309896e0d1f859c9ed41.jpg)

Larch is a very minimal "framework", loosely following the Elm architecture. The main goal of Larch is to provide an easy-to-use, simple, and scalable framework for web apps. Performance is a lesser concern - if performance is an issue, please check out [Om](https://github.com/omcljs/om) or [Re-Frame](https://github.com/Day8/re-frame).

## Handling messages

The [Elm architecture](https://guide.elm-lang.org/architecture/) has 3 separate concerns: model, update and view. Larch handles just the "update" part. It is recommended that you use [DataScript](https://github.com/tonsky/datascript) for your app model, but this is by no means necessary. Larch is unopinionated about how you render your view, you can use any library that can render html from your app model.

The core of Larch is a single function, `msgs->updates!`. Its job is to transform a channel of messages into a channel of model updates. Messages are events that may come from user interaction (e.g. button clicks), or from other sources such as browser events. Updates are a data representation of an update to your app model (a transaction in DataScript).

This transformation is easy when it is a pure, synchronous function. However it becomes more difficult to manage, and test, when messages may trigger asynchronous actions (such as http requests), or need to interact with the (mutable) browser state (e.g. local storage). 

Larch tackles this problem in a similar way to Elm. Each message is transformed into a tuple of `[update, command]` by the `process-msg` function, supplied as an argument to `msg->updates!`. This function is given 2 arguments: the message to process, and the app model. Updates are data structure, and the transformation from message to update is a pure, synchronous function. Every update is put on the channel returned from `msgs->updates!`. A command is an impure or asynchronous function that returns a channel of messages, which is then fed back in to the main message channel.

Commands take 2 parameters: the model, and a map of dependencies. The "dependencies" are provided as an argument to the `msgs->updates!` function. All impure functions that the command calls should be in this map - this makes commands testable. 

This diagram illustrates the overall process:

![msgs->updates! diagram](images/larch-msgs-updates.png)

`msgs->updates!` takes the following arguments:

* `msg-chan` - a channel which outputs all the messages that your app cares about. These messages may be triggered by user interaction, or other global events (loss of internet connection, time changes, etc.). These events can be in any format whatsoever (e.g. strings, keywords, tuples), but it is recommended to use the format `[message-type payload...]`.
* `model` - an atom containing the entire state of your app.
* `dependencies` - a map containing any impure functions and references to mutable objects that your app needs. For example, if your app needs to make http requests, and also update local storage, your dependencies may look like: `{:fetch js/fetch :local-storage js/localStorage}`.
* `process-msg` - a function which takes an event and the model value (i.e. the value of the `model` atom), and returns a tuple of `[update, command]`. Both elements in the tuple are optional - if they are `nil` they will be ignored.

## Updating the model and view

So now we have a channel of updates, but how do we use this to actually update our model and the view? 

Updating the model is easiest if you use DataScript. Each update will be a transaction, so you just have run `transact!` for each update. For example:

``` clojure
(go-loop []
  (let [update (<! updates-channel)]
    (datascript.core/transact! model update)
    (recur)))
```

You can take a similar approach if your model is a standard Clojure data structure, but you will effectively have to implement `transact!` yourself, to update the app model based on the update.

Updating the view depends on which library you use to render your html. If you're using Reagent, then you just have to re-render your app on every model update. You can do this by adding a watch to the model:

``` clojure
(defn reload []
  (reagent/render [root-component @app-model]
                  (.getElementById js/document "app")))

(add-watch app-model :render reload)
```

The same approach should work for all react-based view libraries.

## User Interaction

I've skipped over one very important part so far - how does the user actually interact with the app? 

We need to add a message onto the message channel for each event that we care about. So if the user clicks the submit button in `my-awesome-form` component, we might want to emit a `:my-awesome-form/submit` message. To do this, we can pass the message channel to our root component, then down to the `my-awesome-form` component. The `my-awesome-form` component would then put a new message on the channel in the button click event, like so:

``` clojure
(defn my-awesome-form [message-chan]
  [:form
    [:button {:on-click #(go (put! message-chan [:my-awesome-form/submit %]))}]])
```

## Overall architecture

Now we can use our message channel to update our app model, which we can use to render our view, which in turn generates more messages. The overall app architecture looks very similar to the Elm architecture:

![msgs->updates! diagram](images/larch-architecture.png)

## Examples

For a reference example, please see [monzo-cljs](https://github.com/DaveWM/monzo-cljs).

## License

Distributed under the GPL V3 license.
