# Bialem Testing

Testing rules for Bialem.

## Principle

Run the smallest relevant test scope, not the whole suite every time.

## Backend

- Compile: `./mvnw compile`
- Unit tests: `./mvnw test`
- Integration tests: existing `*ResourceIT` classes

## API Tests

Every new or changed endpoint should have tests covering:

- Authentication (401)
- Authorization (403)
- Validation (400)
- Success cases

## Database Tests

- Migration runs cleanly.
- Relationships and constraints work.
- Unique constraints prevent duplicates.

## Frontend Tests

- Build passes.
- Routes work.
- API calls succeed and fail gracefully.
- Error states are handled.

## Payment Tests

- Success
- Failure
- Duplicate callback
- Refund

## Story / Social Tests

- View
- Duplicate view
- Reaction
- Reaction change
- Unauthorized access

## Running Tests

- Use the smallest relevant scope.
- Example backend single class: `./mvnw test -Dtest=StoryViewResourceIT`
- Example frontend: `npm run build` or targeted component tests.

## Test Data

- Reuse existing test samples and fixtures.
- Do not add production-like secrets to tests.
