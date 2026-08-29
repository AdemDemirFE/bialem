# Bialem Payment Development

Payment features must follow these rules:

- Never store raw card information.
- Use payment provider abstraction.
- Payment status must be persisted.
- Payment callbacks must be idempotent.
- Successful payment creates ticket/order state.
- Failed payments must not create valid tickets.
- Refunds must be tracked.
- Existing Order and User entities must be reused when possible.

Before implementation:

1. Search existing Order entity.
2. Search existing Payment implementation.
3. Search Event/Ticket structures.
4. Search current authentication system.
5. Only create missing structures.