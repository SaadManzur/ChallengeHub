# ChallengeHub
ChallengeHub is a live streaming application that focuses on arranging online challanges (which mostly can be judged by visual means). Each user can nominate, participate or vote on a video broadcasted by a user. Based on the votes, the winner can be eligible for prizes. It also has a Video on Demand system that stores each record, which can later be viewed by each user.

## Background
Hillarious challenges go viral easily through social media with little to no effort e.g. Ice Bucket Challenge, Halloween Candy Challenge etc. This serves as a good source of enterntainment to be true. But it's application can be manifold. 

Human nature drives us to find company. One of the best example is work out. If a person can manage a friend to go with him/her nad hit the gym together, they are more likely to visit the gym next time. An app can do so little to make people social. Even the fitness apps that are available in the market, even though they try to make fitness goal more social, it just does not follow all the way through. Now, why does people stay together on some activity and not on others? Everyone wants an enjoyable time. This is why people like to throw challenges (stupid and funny) and make them go viral.

## What can we learn from this?
If we can define each activity that conforms unity and merriment, people will more stick together. And adding rewards can attract specific gorups of people to come forward from all over the world to do some tasks that they were less likely to do. For example, the highest push-ups in a day will get 100$. Now things become a bit interesting.

Sure, we can throw the challenge, how to monitor it? Basically make them go live, and let people decide or if there are good vision specific learner, let it decide.

## Benefits and Business Model
* **Advertisement** This platform can be a good medium for advertisement. Any company who wants to advertise their brand name, can register a challenge. People participate and get reward from them and get to know their products even. And they pay us partially. ***What if enough people don't participate?***.... Then they don't have to pay. They pay proportional to the coverage they receive.

* **Data** Data is the key component that drives most of the technologies nowadays. It indeed serves as a valuable source of visual data.
  * **Vision** For Computer Vision scientist, labeled vision data is treasure trove. Now, since every one will be doing the same challenge the data will already come labeled. It can easily be used to learn motion capture.
  * **Behavior** People's fitness and behavior can also be tracked through the app.
  
Now, all of the data, of course comes under the jurisdiction of the user itself. Whether the user him/herself is willing to grant permission to the data.

## Architecture
The solution has three core components -
* Live Streaming Server
* Application
* Backend API

The application directly communicates with the Live Streaming Server and API. The Live Streaming Server handles the streaming accross multiple devices and recording the video for "Video on Demand". The application has interface for participating in a challenge, voting on a challenge, nominating a challenge (not implemented on this Hackathon, but backend API present) and watching "Video on Demand" (not implemented, videos are stored in the server, but requirement of integrating a video player was a little bit out of scope of the hackathon.)

## Technologies
* Red5Pro Server and SDK (for Live Streaming)
* Android (Java)
* ExpressJS (Backend)
* SendGrid API
* Google Cloud Platform

***Nuances*** The nominated challenge triggers a mail to potential users and if the user interacts with the mail, the challenge is thought to be more "popular".
