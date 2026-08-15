# AI Usage

AI tools were used as an engineering assistant, not as an authority. I supplied the assignment constraints, desired invariants, API behavior, and relevant implementation context, then reviewed and tested generated suggestions.

## Example 1 — Inventory concurrency

### AI suggestion
Read `available_units` into Java, check whether enough inventory exists, and then update the row.

### Decision
Rejected.

### Technical reason
The read/check/update sequence has a race condition. Two concurrent requests can both observe the same availability and oversell.

### Chosen approach
Use a single atomic conditional SQL update:

```sql
UPDATE drops
SET available_units = available_units - :quantity
WHERE id = :dropId
AND available_units >= :quantity;
```

### Verification
The repository contract treats zero affected rows as insufficient inventory. This keeps the correctness boundary inside MySQL.

---

## Example 2 — Redis as the inventory authority

### AI suggestion
Keep the inventory counter in Redis and use Redis atomic decrement operations as the primary reservation mechanism.

### Decision
Rejected.

### Technical reason
Redis would become a correctness dependency. A cache failure or cache/database divergence could make inventory correctness unreliable.

### Chosen approach
MySQL remains the source of truth. Redis caches availability for fast reads and is evicted after inventory-changing operations.

### Verification
The reservation path can operate without reading Redis.

---

## Example 3 — Publish RabbitMQ directly after database commit

### AI suggestion
Commit the reservation transaction and then publish the RabbitMQ event.

### Decision
Rejected.

### Technical reason
The database commit and RabbitMQ publish are two independent operations. The database can commit successfully while RabbitMQ is unavailable, causing an event to be lost.

### Chosen approach
Write an outbox event in the same MySQL transaction. A scheduled publisher retries unpublished events.

### Verification
The outbox row is committed together with the reservation state. A failed publish leaves `published_at` null so a later poll retries it.

---

## Example 4 — Use a distributed Redis lock for every reservation

### AI suggestion
Acquire a Redis distributed lock per drop before modifying inventory.

### Decision
Rejected for the core correctness path.

### Technical reason
It adds another distributed coordination mechanism without being necessary for the database invariant. MySQL can atomically enforce `available_units >= quantity`.

### Chosen approach
Use the database conditional update as the concurrency primitive.

### Verification
The SQL statement itself establishes the required invariant without requiring a Redis lock.

---

## How I validated AI-generated output

I reviewed generated code against the assignment's explicit requirements and the following invariants:

1. A reservation must never make available inventory negative.
2. An active hold can only transition once.
3. Cancellation/expiration releases inventory exactly once.
4. Confirmation must not succeed after expiration.
5. Database state remains the source of truth.
6. Redis and RabbitMQ failures must not silently corrupt reservation state.

I also added unit tests around the highest-risk service behavior. AI-generated code was treated as a draft and manually reviewed before being included.
