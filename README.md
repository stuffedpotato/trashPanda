# Trash Panda

## Inspiration
We were inspired by the amazing work that [FoodStash](https://www.foodstash.ca) does at a large scale — rescuing over **130,000+ pounds of food per month** from grocery stores, restaurants, and farms. While their impact is incredible, we wanted to build something that helps everyday people take action *locally*.

Let’s face it: most of us want to avoid food waste, but we get busy or lazy, and it feels like a hassle to donate one or two extra items. So, what if you could help reduce waste without leaving your block?

## What it does
**Trash Panda** is a local food-sharing platform that helps neighbors:
- Share extra ingredients they have
- Generate fun recipes based on available items

And if you don’t find a match and your avocado is on death row? We’ve got you. We use the **Gemini API** to whip up recipes using *what you have*, so nothing has to go to waste.

It’s sustainability, made social (and a little silly).

## How we built it
We built our backend in **Java** using the **Spark framework**, following an object-oriented design. We used **PostgreSQL** for our database, and implemented secure password hashing.

The frontend is written in **HTML** and dynamically interacts with the backend via RESTful APIs. For recipe generation, we integrated the **Gemini API**, which turns shared items into usable meal ideas.

## Challenges we ran into
Frontend integration was tough — most of us had very little experience with frontend development, so getting everything to work smoothly took time and trial-and-error.

We were also complete beginners to working with databases, so designing schemas, writing SQL queries, and connecting everything took serious learning and debugging.

## Accomplishments that we're proud of
Despite the learning curve, we were able to:
- Build secure login/signup functionality
- Enable food sharing and matching
- Generate real-time recipes based on shared items (or only your own!)
- Create a fun and functional interface for users

## What we learned
One big lesson: **don’t overscope**.

We had so many features in mind at the start, but we quickly (or not-so-quickly) realized that learning a brand new stack while trying to ship a polished product in a short time meant we had to be smart and focused. Still, the challenge pushed us to grow and problem-solve fast.

## What's next for Trash Panda
We’d love to keep working on this project! Next up:
- Add a **chat feature** so users can message each other directly
- Let users set up **recurring donation pickups**
- Improve the UI/UX with filters, images, and accessibility features

We hope Trash Panda can be a small step toward big change, one leftover ingredient at a time. 🐼🥦
