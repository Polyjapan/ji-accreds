# ji-accreds
A system to manage accreditations at Japan Impact

# Use-cases workflows

## Gold Tickets badges

1. **User** buys ticket on the shop
2. **Shop** creates an already accepted request for the user (bound to its uid) on accred and redirects the user there. (He has to login again)
3. **User** is redirected directly to the accred creation page, where he uploads the requested information. (He might have to fill in multiple badges)
4. **Admin** validates the badge on the accred system. It can no longer be modified.

We can:
- Refuse a badge. This sends an email to the user so that he can fix his request.
- See all missing badges. By looking at the draft `gold` badges 

During the event, the staff goes on accred, finds the accred, delivers the badge and tag it as delivered.

## Booth guests

### Current workflow (no Pro intranet)

1. **Pro** sends the link to accred to the booth manager
2. **Booth manager** goes to the accred dashboard
3. **Booth manager** opens the form on accred and sends a request, providing the name of the booth and the number of requested accreds
4. **Pro** accepts
5. **Booth manager** goes to the accred creation page, where he provides the identities for each requested accred. 
6. **Booth manager** can edit the identities until they are delivered

During the event, the staff goes on accred, finds the accred, delivers the wristband and tag it as delivered. It is done on a per-accred basis rather than per request basis.


### Future workflow (Pro intranet)

1. **Pro Intranet** creates an already accepted request for the user (bound to its uid) on accred, using the number of invites requested by the booth manager
2. **Booth manager** goes to the accred creation page, where he provides the identities for each requested accred. 
3. **Booth manager** can edit the identities until they are delivered

During the event, the staff goes on accred, finds the accred, delivers the wristband and tag it as delivered. It is done on a per-accred basis rather than per request basis.

The other form can still be kept to allow booth managers to request more accreditations.

## Booth parking authorization

### Current workflow (no Pro intranet)

1. **Pro** sends the link to accred to the booth manager
2. **Booth manager** goes to the accred dashboard
3. **Booth manager** opens the form on accred and sends a request, providing the name of the booth and the number of requested authorizations
4. **Pro** accepts
5. **Booth manager** goes to the accred creation page, where he provides the license plate for each of the authorizations
6. **Booth manager** can edit an authorization until he validates it
7. **Booth manager** validates the authorization and can print it immediately

The printed authorization is anti-piracy secured (complex shapes to avoid photoshopping).

### Future workflow (Pro intranet)

1. **Pro Intranet** creates an already accepted request for the user (bound to its uid) on accred, using the number of invites requested by the booth manager
2. **Booth manager** goes to the accred creation page, where he provides the license plate for each of the authorizations
3. **Booth manager** can edit an authorization until he validates it
4. **Booth manager** validates the authorization and can print it immediately

The printed authorization is anti-piracy secured (complex shapes to avoid photoshopping).

The other form can still be kept to allow booth managers to request more authorizations.

## Comitee badges (and all accrediting badges)

### For the official comitees and team members

1. **Security manager** uses a bulk import to generate the requests, providing for each badge all the requested info (name, pole, security clearance, email)
2. Badge requests are generated and accepted, not bound to any account, and sent by email
3. **Comitee member** clicks on the link, and logs in. The request becomes bound to the account.
4. **Comitee member** fills in the accred creation form (provides his picture)

### For individuals comitee members (some special staffs)

Same thing, without the bulk.

1. **Security manager** creates a request with the required info (name, pole, security clearance, email)
2. Badge request is generated and accepted, not bound to any account, and sent by email
3. **Individual** clicks on the link, and logs in. The request becomes bound to the account.
4. **Individual** fills in the accred creation form (provides his picture)

If we want to generate the badge with no interaction from the user (for example, for EPFL security), we provide a fake email and fill the request directly.


Then, the comitee who prints the badge will mark it as printed, avoiding double prints.

## Staff badges

1. **Staff intranet** sends an accepted request for each staff, with the picture left to complete
2. **Staff** logs in to the dashboard, opens the accepted request, goes to the accred creation page, provides picture, and sends
3. **Staff manager** checks the picture and accepts

Then, the comitee who prints the badge will mark it as printed, avoiding double prints.

## Guests / VIPs

1. **Pro manager** creates a badge with the required info (name). The badge is not bound to any request.

## Medias

1. **Comm** sends the link to accred to the media person
2. **Media person** goes to the accred dashboard
3. **Media person** opens the form on accred and sends a request, providing some info (name of the media, website...) and the number of requested authorizations
4. **Comm** accepts
5. **Booth manager** goes to the accred creation page, where he provides the license plate for each of the authorizations
6. **Booth manager** can edit an authorization until he validates it
7. **Booth manager** validates the authorization and can print it immediately

## How it works? (first draft, wip)


### Accreditation Request

The user requests the accreditations himself. He fills a form, filing an `ACCRED REQUEST`. 

Once accepted, one (or several) accreditations are created. The user then have to fill the accreditations.


#### States

A request evolves between different states:

```
    DRAFT <-------------------\
      ^                       |
      |     /---------> CHANGES REQUESTED 
      V    / 
      	  /
    SENT -------------> REFUSED
      |
      |
      V

  ACCEPTED    
```

The request stays in state `DRAFT` until the user asks for validation. It is then moved to `SENT` state. The user can still change the request, in which case it will go back to `DRAFT` state.

An admin will then validate the request. He can either accept, refuse, or request modifications. `ACCEPTED` and `REFUSED` states are final. A request in this state can no longer be updated. `CHANGES REQUESTED` requests can be edited. They will be moved to draft as soon as they are updated.

All state changes are logged: `timestamp, from_state, to_state, changed_by, reason`. The reason can be automatically filled, but needs to be provided by the admin for the `REFUSED` and `CHANGES REQUESTED` states.

#### Example requests

##### Media accreditation

This accredidation is requested directly from the accred website.

- Identity (firstname, lastname, birthdate, email) of requestor
- Name of the media
- Website of the media
- List of requested accreditations. For each:
	- Identity of the person (firstname, lastname, birthdate, email)


##### Parking authorization

This request is first created by an other service (or an admin).

Data provided by the external service:
 - Name of the booth
 - Identity of requestor

Data provided by the user:

- Reason why you need a parking authorization
- List of requested authorizations. For each:
	- License plate number, vehicle type, parking duration requested

When accepted, the parking authorizations are immediately generated and sent.

### Accreditation

Once an accreditation request is accepted, an accreditation is generated. It can be of three types: 
 - `badge`, for a physical badge that needs to be printed
 - `authorization`, for a physical authorization that needs to be printed by the user
 - `accreditation`, for any other kind of accreditation (wristband, ...)

The accreditation can also be directly generated by an admin or external app.

The accreditation also holds a boolean flag, `requires_validation`. If this flag is set to `true`, the accreditation needs to be validated by the admins before being accepted. If this flag is set to `false`, the accreditation is directly accepted when sent by the user. This make little sense for a badge, but makes more sense for an authorization (the user can directly print it, for example).

An accreditation contains multiple fields. Each field has a type, a position, and a value source. 

Types:
 - image
 - short text

Value source:
 - `fixed`: the value is a constant
 - `admin`: the value is provided when the accreditation is created, by the admin or external app creating it
 - `user`: the value is provided by the user

The accreditation has a state too.


```
    DRAFT <-------------------\
      ^                       |
      |     /---------> CHANGES REQUESTED 
      V    / 
      	  /
    SENT -------------> REFUSED
      |
      |
      V

  ACCEPTED    -----> PRINTED
```

If `requires_validation` is set to `false`, the state moves directly from `DRAFT` to `ACCEPTED`.

