# Granite

Granite is a framework for writing client side applications in Scala.js.


## Goals

- Type safety, most JS frameworks are not type-safe which leads to large code-bases being hard to maintain. Granite aims to 
  give you a type safe way to write web applications. 

- More structure than React, less complexity than Angular. React is great but it only provides the view layer so you find yourself pulling in a bunch of libraries to fill in the gaps - the only problem is there is an almost overwhelming amount of options due to library fragmentation. Angular is on the opposite end of the spectrum where it provides a ton of structure - so much to the point that it introduces a plethora of framework specific terminology. Granite aims to land somewhere in the middle, it borrows a lot of the good ideas from React & Angular but provides a more comprehensive & structured framework (model, view and controller) than Raect and has only a fraction of the complexity of Angular.

- Good performance. Granite doesn't aim to be blazingly fast, but it aims to be "fast enough" for 99% of typical use cases.

- Easy to understand. Assuming proficiency in the Scala language & HTML, Granite only has a handful of ideas you need to be comfortable with: Model (really just standard Scala case classes), Component (View), Controller & Events 

- Easy to test: By encouraging type-safety and functional purity (where possible), Granite applications are very easy to unit test

- Minimize decision fatigue, but give the option to make intelligent tradeoffs. For example when it comes to view-templating  you often have to make a choice between being friendly to programmers (ex write templates in Scala to get full abstraction abilities) or being friendly to designers (give up some abstraction but write plain HTML with mustache style variable bindings). Granite's Components can accomidate either - if you want to be friendly to pogrammers you should use ScalaTags
  for Components and if you want to be friendly to designers you shoul use Mustache templates (support for this coming soon). 

## Getting Started

Clone the repository, check out the examples/ directory for a TodoMVC example



