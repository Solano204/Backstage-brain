/*
 * BrainTrust Backstage Backend
 * Production-ready backend configuration with custom authentication
 */

import { createBackend } from '@backstage/backend-defaults';
import { createBackendModule } from '@backstage/backend-plugin-api';
import { stringifyEntityRef } from '@backstage/catalog-model';
import { 
  authProvidersExtensionPoint,
  createOAuthProviderFactory,
} from '@backstage/plugin-auth-node';
import { githubAuthenticator } from '@backstage/plugin-auth-backend-module-github-provider';

// ============================================================================
// CUSTOM AUTHENTICATION MODULE
// ============================================================================

/**
 * Custom GitHub Sign-In Resolver
 * This allows users to sign in without existing in the catalog
 */
const customGitHubAuthModule = createBackendModule({
  pluginId: 'auth',
  moduleId: 'custom-github-auth-resolver',
  register(reg) {
    reg.registerInit({
      deps: {
        providers: authProvidersExtensionPoint,
      },
      async init({ providers }) {
        providers.registerProvider({
          providerId: 'github',
          factory: createOAuthProviderFactory({
            authenticator: githubAuthenticator,
            async signInResolver({ result }, ctx) {
              // Use GitHub username instead of email
              const username = result.fullProfile.username;
              
              if (!username) {
                throw new Error('GitHub username not found in profile');
              }

              // Create user entity reference
              const userEntityRef = stringifyEntityRef({
                kind: 'User',
                name: username.toLowerCase(), // GitHub usernames in lowercase
                namespace: 'default',
              });

              // Issue authentication token
              return ctx.issueToken({
                claims: {
                  sub: userEntityRef,
                  ent: [userEntityRef],
                },
              });
            },
          }),
        });
      },
    });
  },
});

// ============================================================================
// BACKEND INITIALIZATION
// ============================================================================

const backend = createBackend();

// ----------------------------------------------------------------------------
// Core Plugins
// ----------------------------------------------------------------------------

// App Backend (serves the frontend)
backend.add(import('@backstage/plugin-app-backend'));

// Proxy Backend (for external API calls)
backend.add(import('@backstage/plugin-proxy-backend'));

// ----------------------------------------------------------------------------
// Authentication & Authorization
// ----------------------------------------------------------------------------

// Auth Backend
backend.add(import('@backstage/plugin-auth-backend'));

// GitHub OAuth Provider
// backend.add(import('@backstage/plugin-auth-backend-module-github-provider'));

// Custom GitHub Auth Resolver (our custom module)
backend.add(customGitHubAuthModule);

// Guest Provider (optional - for development/testing)
// Comment this out in production if you want to enforce GitHub login
 backend.add(import('@backstage/plugin-auth-backend-module-guest-provider'));

// Permission Backend
backend.add(import('@backstage/plugin-permission-backend'));

// Permission Policy - CHANGE THIS IN PRODUCTION!
// For development: allow all
backend.add(
  import('@backstage/plugin-permission-backend-module-allow-all-policy'),
);
// For production: implement custom policy
// backend.add(import('./modules/permission-policy'));

// ----------------------------------------------------------------------------
// Software Catalog
// ----------------------------------------------------------------------------

// Catalog Backend
backend.add(import('@backstage/plugin-catalog-backend'));

// Catalog Entity Model for Scaffolder
backend.add(
  import('@backstage/plugin-catalog-backend-module-scaffolder-entity-model'),
);

// Catalog Error Logging
backend.add(import('@backstage/plugin-catalog-backend-module-logs'));

// GitHub Catalog Integration
backend.add(import('@backstage/plugin-catalog-backend-module-github'));

// GitHub Org Data Provider (imports teams and users from GitHub)
backend.add(import('@backstage/plugin-catalog-backend-module-github-org'));

// ----------------------------------------------------------------------------
// Software Templates (Scaffolder)
// ----------------------------------------------------------------------------

// Scaffolder Backend
backend.add(import('@backstage/plugin-scaffolder-backend'));

// GitHub Actions for Scaffolder
backend.add(import('@backstage/plugin-scaffolder-backend-module-github'));

// GitLab Actions for Scaffolder (if you use GitLab)
// backend.add(import('@backstage/plugin-scaffolder-backend-module-gitlab'));

// Bitbucket Actions for Scaffolder (if you use Bitbucket)
// backend.add(import('@backstage/plugin-scaffolder-backend-module-bitbucket'));

// Notifications for Scaffolder
backend.add(
  import('@backstage/plugin-scaffolder-backend-module-notifications'),
);

// ----------------------------------------------------------------------------
// TechDocs
// ----------------------------------------------------------------------------

// TechDocs Backend
backend.add(import('@backstage/plugin-techdocs-backend'));

// ----------------------------------------------------------------------------
// Search
// ----------------------------------------------------------------------------

// Search Backend
backend.add(import('@backstage/plugin-search-backend'));

// PostgreSQL Search Engine
backend.add(import('@backstage/plugin-search-backend-module-pg'));

// Search Collators
backend.add(import('@backstage/plugin-search-backend-module-catalog'));
backend.add(import('@backstage/plugin-search-backend-module-techdocs'));

// ----------------------------------------------------------------------------
// Kubernetes
// ----------------------------------------------------------------------------

// Kubernetes Backend (for viewing K8s resources)
backend.add(import('@backstage/plugin-kubernetes-backend'));

// ----------------------------------------------------------------------------
// Additional Integrations
// ----------------------------------------------------------------------------

// Notifications Backend
backend.add(import('@backstage/plugin-notifications-backend'));

// Signals Backend (real-time updates)
backend.add(import('@backstage/plugin-signals-backend'));

// GitHub Actions Plugin Backend (for CI/CD visibility)
// Note: This is optional, the frontend plugin works without it
// backend.add(import('@backstage/plugin-github-actions-backend'));

// ----------------------------------------------------------------------------
// Events System (optional - for webhooks)
// ----------------------------------------------------------------------------

// Events Backend
// backend.add(import('@backstage/plugin-events-backend'));

// GitHub Events (webhooks)
// backend.add(import('@backstage/plugin-events-backend-module-github'));

// ----------------------------------------------------------------------------
// DevTools (optional - for debugging)
// ----------------------------------------------------------------------------

// DevTools Backend (useful for development)
// backend.add(import('@backstage/plugin-devtools-backend'));

// ============================================================================
// START BACKEND
// ============================================================================

backend.start();