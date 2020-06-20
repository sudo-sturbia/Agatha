/**
 * Provides the classes that represent Agatha's client verification
 * system.
 * <p>
 * The sub-system runs parallel to the main thread of the server
 * and is used to verify user credentials, and existence of usernames
 * to help avoid conflicts. It also implements a locking mechanism
 * for accounts if the client attempts several failed logins.
 */
package com.github.sudo_sturbia.agatha.server.clients;