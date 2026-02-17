const { Client } = require('pg');

const client = new Client({
  host: 'localhost',
  port: 5433,
  user: 'postgres',
  password: 'backstage',
  database: 'backstage',
});

console.log('Testing PostgreSQL connection from Node.js...');
console.log('Connection config:', {
  host: 'localhost',
  port: 5432,
  user: 'postgres',
  password: '***',
  database: 'backstage',
});

client.connect()
  .then(() => {
    console.log('✓ Successfully connected to PostgreSQL!');
    return client.query('SELECT version()');
  })
  .then((res) => {
    console.log('✓ PostgreSQL version:', res.rows[0].version);
    return client.end();
  })
  .then(() => {
    console.log('✓ Connection test passed!');
    process.exit(0);
  })
  .catch((err) => {
    console.error('✗ Connection failed:', err.message);
    console.error('Error details:', err);
    process.exit(1);
  });