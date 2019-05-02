# ji-accreds
A system to manage accreditations at Japan Impact

# Use-cases workflows

## Gold Tickets badges

1. **User** buys ticket on the shop
2. **Shop** creates an already accepted request for the user (bound to its uid) on accred and redirects the user there. (He has to login again)
3. **User** is redirected directly to the accred creation page, where he uploads the requested information. (He might have to fill in multiple badges)
4. **Admin** validates the badge on the accred system. It can no longer be modified.

We can:
- Refuse a badge. This sends an email to the user so that he can fix his request.
- See all missing badges. By looking at the draft `gold` badges 

During the event, the staff goes on accred, finds the accred, delivers the badge and tag it as delivered.

## Booth guests

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

## Booth parking authorization

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

### For the official comitees and team members

1. **Security manager** uses a bulk import to generate the requests, providing for each badge all the requested info (name, pole, security clearance, email)
2. Badge requests are generated and accepted, not bound to any account, and sent by email
3. **Comitee member** clicks on the link, and logs in. The request becomes bound to the account.
4. **Comitee member** fills in the accred creation form (provides his picture)

### For individuals comitee members (some special staffs)

Same thing, without the bulk.

1. **Security manager** creates a request with the required info (name, pole, security clearance, email)
2. Badge request is generated and accepted, not bound to any account, and sent by email
3. **Individual** clicks on the link, and logs in. The request becomes bound to the account.
4. **Individual** fills in the accred creation form (provides his picture)

If we want to generate the badge with no interaction from the user (for example, for EPFL security), we provide a fake email and fill the request directly.


Then, the comitee who prints the badge will mark it as printed, avoiding double prints.

## Staff badges

1. **Staff intranet** sends an accepted request for each staff, with the picture left to complete
2. **Staff** logs in to the dashboard, opens the accepted request, goes to the accred creation page, provides picture, and sends
3. **Staff manager** checks the picture and accepts

Then, the comitee who prints the badge will mark it as printed, avoiding double prints.

## Guests / VIPs

1. **Pro manager** creates a request to group different badges for a same activity or prestation
2. **Pro manager** creates accreds within the request

## Medias

1. **Comm** sends the link to accred to the media person
2. **Media person** goes to the accred dashboard
3. **Media person** opens the form on accred and sends a request, providing some info (name of the media, website...) and the number of requested authorizations
4. **Comm** accepts
5. **Media person** goes to the accred creation page, where he provides the identities for each requested accred. 
6. **Media person** can edit the identities until they are delivered

# Some interesting properties

- Admins can impersonate users and create requests on their behalf. 
	- They need to provide an email address when doing that
	- When the request is accepted, an email is sent to the provided address to allow the user to claim the request
	- This email can also be sent at any time by the admin.
- Admins can edit request and accreditations, with the exception of accepted/refused requests and of delivered accreditations (i.e. delivered badge, wristband, ...)
- Admins can create as many accreditations as they want within a request when accepting it
	- The number of accreditations delivered by admins is not always the same as the number requested by the user 
	- The admin chooses the type of accreditation on a one-by-one basis, and not depending on the request

Also, it is interesting to note that in all these workflows the user (or an admin impersonating him) will have to provide identification info (license plate, name, picture, ...). By separating request and accreds, we allow the user to make the request early, without knowing precisely who will help him, while taking more time to actually fill in the accreds.



# How it works?

The workflow is always separated in two parts.

## 1. The Accreditation Request (`request`)

### Properties

- `user`: the userId (against CAS) of the person who manages the request. Can be null, if the request is not yet linked to a user. 
- `claim_code`: a random code, allowing to claim the current request while logged in. Usually null.
- `type`: a link to a description of the fields needed for the accreditation

The fields have following type: 
- `text` and `longText`
	- optional: `minLength`
	- optional: `maxLength`
	- optional: `regex`
- `email`
- `date`
- `checkbox`
- `select`
- `file`
- `image`
- `url`

They also carry:
- `name`: the name of the field in the form
- `label`: the label of the field
- optional `helpText`: some help to fill the line
- `required`: if true, this field is mandatory

### Database Structure

```
editions:
	id: int
	endDate: timestamp # automatic archival date
	name: string # ex "Japan Impact 11"

request_types:
	id: int
	edition: int -> editions#id
	name: string # ex "Media Accreditation Request"
	required_group: optional string # provides a group that the user must have to be able to start this request type from the front office
	hidden: boolean # if true, the request type cannot be started from the front office

request_type_fields:
	id: int
	request_type: int -> request_types#id
	name: string
	label: string
	help_text: optional string
	required: boolean
	type: string, within set described before

request_type_fields_aditional: 
	field: int -> request_type_fields#id
	key: string
	value: string

	# Examples :
	# for a text
	#	key=minLength value=10
	#
	# for a select, allows to provide the set of allowed values. Will be rendered with key as the value and value as the label

request:
	id: int
	user_id: optional int
	claim_code: optional string
	request_type: int -> request_types#id
	state: SET('draft', 'sent', 'requested_changes', 'accepted', 'refused')

request_logs:
	request_id: int -> requests#id
	from_state: optional SET('draft', 'sent', 'requested_changes')
	to_state: SET('draft', 'sent', 'requested_changes', 'accepted', 'refused')
	reason: optional string
	timestamp: timestamp
	changed_by: int # cas user id

request_content:
	request_id: int
	field_id: int
	value: string
```

### Flows

#### Automatic

An external app creates an automatically accepted request, linked to a given user.

This allows an intranet (or the shop) to automatically grant an accred that should be granted anyway (for example, gold ticket).

#### User creation

1. An user creates the request, updates it, and sends it when ready.
2. The request is then reviewed by an admin that can `ACCEPT`, `REFUSE` or `REQUEST CHANGES`.
	- If refused, the request doesn't move forward and cannot be modified
	- If changes requested, the user goes back to 1 and updates the request

An admin can also create a request on behalf of a user. If so, it works as follows:

1. Admin creates request. A `claim_code` is generated.
2. Admin can send the request at any time by providing an email address. An email will be sent with the claim code. Once claimed, the request becomes a "standard" user request, that can be edited by the user directly.
3. When accepting the request, an email **must** be provided if the request `claim_code` is not null. 

An admin can always edit other users' requests, hence the second workflow is not so different from the first one. The main difference being that in the second case, `claim_code` is not null, and `user` is null. 

## 2. The Accreditation (`accred`)

### Properties

Very similar to requests.

- `self_service`: an accreditation that the user can validate himself (wristband, parking authorization)
- `printable`: 
	- if `self_service` is `true`: an accreditation that the user can print himself (parking authorization)
	- if `self_service` is `false`: an accreditation that the comitee needs to print (badge)

### Database Structure


```
accred_types:
	id: int
	edition: int -> editions#id
	name: string # ex "Badge comité" "Bracelet exposant" "Autorisation de parking longue durée" "Badge non nominatif"...
	is_self_service: boolean
	is_printable: boolean

accred_type_fields:
	id: int
	accred_type: int -> accred_types#id
	name: string
	label: string
	help_text: optional string
	required: boolean
	type: string, within set described before
	show_in_listings: boolean # describes if this field should be displayed on the accreditations listings that will be used by the staff
	user_editable: boolean # if false, the value is provided by the entity creating the accred and cannot be edited by the user. if true, it is provided by the user.

accred_type_fields_aditional: 
	field: int -> accred_type_fields#id
	key: string
	value: string

	# Examples :
	# for a text
	#	key=minLength value=10
	#
	# for a select, allows to provide the set of allowed values. Will be rendered with key as the value and value as the label

accred:
	id: int
	request_id: int -> requests#id
	accred_type: int -> accred_types#id
	state: SET('draft', 'sent', 'requested_changes', 'accepted', 'printed', 'delivered')

accred_logs:
	request_id: int -> requests#id
	from_state: SET('draft', 'sent', 'requested_changes', 'accepted', 'printed')
	to_state: SET('draft', 'sent', 'requested_changes', 'accepted', 'printed', 'delivered')
	reason: optional string
	timestamp: timestamp
	changed_by: int # cas user id

accred_content:
	accred_id: int
	field_id: int
	value: string
```

TODO: a few more tables for images generation :)

### Flow

1. The entity accepting the request (admin or app) creates an accreditation. It can be done at any time for an accepted request. Usually, all accreditations are generated immediately after the request is accepted. Sometimes, however, an admin or an app can add more accreditations after. Sometimes, some values need to be provided when creating the accreditation (for example, a badge clearance level)
2. The user can see all accreds linked to a request from the request page. He cannot create them but can edit them, to provide the requested information. Some fields might not be editable (see db schema).
3. Then, if the accreditation is a `self_service` accred (like an authorization to print, or a wristband to take):
	1. The user can accept the accred himself
	2. The user can un-accept and edit the accred as long as it has not been `DELIVERED`
	3. If the accred is a `printable` accred (like an authorization to print), the user can deliver it himself. The printable media will be generated and the accred marked as `DELIVERED`.
4. If the accreditation is not a `self_service` accred (like a badge):
	1. The user sends his accred
	2. An admin `ACCEPTS` or `REQUESTS CHANGES`.
	3. If accepted, the user can no longer change his accred. In the other case, he has to do the requested changes and send again.
5. When the accreditation is delivered (self print, wristband given, badge given), the accred is tagged as `DELIVERED` and "archived". Nothing and nobody can change it anymore.
