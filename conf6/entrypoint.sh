#!/bin/bash
redis-server /usr/local/etc/redis/redis.conf &
sleep 5  # Redis 서버가 완전히 실행될 때까지 대기
redis-cli -p 7005 FLUSHALL
redis-cli -p 7005 CLUSTER RESET
wait