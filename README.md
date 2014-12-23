LeyLinesClient
==============
Project Leylines
v0.01

Client side 
[server is in leylines github repo]

Contributors:
* Tamel Nash
* Cameron Rivera
* Travis Geeting
* Zachary Hutchinson

-- Goal --

Path prediction and deviation done dynamically based on historical data and user settings. 

-- What it is --

Leylines is a tool. We aim to create a product that can be of use in determining whether a person, object or porcupine has violated its historical routine for a variety of types. When the user determined threshold has been reached an alert is sent out (presently via email) to a number of concerned recipients.

Leylines aims to be as automatic as possible. This means, no user interaction, other than initial setup, is required. We believe in data ownership, meaning this software is not meant for server farms, but is meant for private installation on user owned hardware.

Leylines consists of a server and client side. The server can service multiple clients, but, at present, is not meant to be used on a massive scale. Our scope is small--a family, an organization, or group of friends--not a small country. We are targeting a self-contained group that remains more or less static. 

-- Future --

Our present and future goals are to refine the path prediction algorithm that isolates deviations from the norm, and to cross-reference that with contextual data and/or destination prediction. What we hope to achieve is something that keeps an updated picture of a person or object's normal movements in context to its environment and schedule through manually or automatically created tags.

-- Version History --

v0.01: Infrastructure and proof of concept. 
