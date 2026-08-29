# Bialem Payments

Payment and ticket rules for Bialem.

## Scope

- Event tickets
- Ticket types
- Orders
- Order items
- Payments
- Payment providers
- iyzico
- Webhook / callback
- Refund
- Payment status
- Transaction ID
- Idempotency
- QR tickets

## Rules

- Never store card data in the database.
- Use a payment provider abstraction.
- Keep provider-specific code behind the abstraction.
- A ticket is VALID only after successful payment.
- Callbacks must be idempotent.
- The same callback processed twice must not create duplicate orders or tickets.
- Refunds must be tracked.
- Secrets and keys must not be committed to Git.

## Provider Abstraction

- Define a provider-agnostic payment service interface.
- Implement iyzico (or other providers) behind that interface.
- Business logic depends on the abstraction, not the provider.

## Order / Order Items

- Create order before payment.
- Link order items to tickets or products.
- Track order status explicitly.

## Payment Status

- Possible states: PENDING, SUCCESS, FAILED, REFUNDED, etc.
- State transitions must be explicit and logged.

## Callback / Webhook

- Validate callback signature where supported.
- Use transaction ID + idempotency key to avoid duplicates.
- Return appropriate HTTP status to the provider.

## Refund

- Track refund requests and provider responses.
- Update ticket/order status accordingly.

## QR Tickets

- Generate QR only for validated tickets.
- Verify QR against the backend on scan.

## Secrets

- Keep provider keys in environment variables or secret managers.
- Never write them in source code or config files.

## Tests

- Success
- Failure
- Duplicate callback
- Refund
