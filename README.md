# AllMail

AllMail is a full-stack email workspace that brings multiple inboxes into one clean, centralized dashboard.

The app is built for people who manage different email accounts for school, work, personal life, job searching, or side projects and want one place to quickly check what needs their attention without constantly switching accounts.

<img width="1364" height="641" alt="AllMail login page screenshot" src="https://github.com/user-attachments/assets/a8c64bbb-6029-41b9-b5ee-40e1cb247704" />

## Tech Stack

### Frontend

- React
- TypeScript
- Vite
- Bootstrap

### Backend

- Java
- Spring Boot
- Spring Security
- OAuth2 Login
- Spring Data JPA
- PostgreSQL
- Gmail API

## Current Status

AllMail currently supports Google-based login and Gmail inbox previews. Users can connect Google accounts, view messages from all active connected accounts, and switch between the unified inbox and individual account inbox views.

The project is actively being expanded toward a more complete email management workspace.

## Roadmap

Planned improvements include:

- Paginated mailbox endpoints
- Account-specific backend inbox fetching
- Full message detail fetching
- Delete, reconnect, and manage connected accounts from the UI
- Persistent in-app notifications
- Message actions such as archive, delete, star, and mark as read/unread
- Compose and send email
- Outlook/Microsoft account support
- Smarter filtering, labels, and account organization
- Database-backed email summary caching for better performance at scale
