#!/bin/bash
#通知dsp端大数据部分已处理完成
curl -d "key=f503ecfe5b136ddd05126646496f764c&id=${1}" http://${2}/ads/callback
