import requests
import time
BASE_URL = 'http://127.0.0.1:8080'
STATUS_OK = requests.codes['ok']
STATUS_BAD_REQUEST = requests.codes['bad']
STATUS_NOT_FOUND = requests.codes['not_found']

def test_creat_bucket_proper_name():
    """POST /status should have status_code 200"""
    bucketname = 'demo'
    resp = requests.post(BASE_URL + '/' + bucketname + '?create')
    assert resp.status_code == STATUS_OK

def test_create_bucket_dity_name():
    """POST /{bucketname}?create should have status_code 400"""
    bucketname = 'demo.'
    resp = requests.post(BASE_URL + '/' + bucketname + '?create')
    assert resp.status_code == STATUS_BAD_REQUEST

def test_drop_bucket_exist_name():
    time.sleep(2)
    """Delete /{bucketname}?delete should have status_code 200"""
    bucketname = 'demo'
    resp = requests.delete(BASE_URL + '/' + bucketname + '?delete')
    assert resp.status_code == STATUS_OK

def test_drop_bucket_not_exist_name():
    """Delete /{bucketname}?delete should have status_code 400"""
    bucketname = 'abc'
    resp = requests.delete(BASE_URL + '/' + bucketname + '?delete')
    assert resp.status_code == STATUS_BAD_REQUEST

def test_creat_bucket_proper_name():
    """POST /status should have status_code 200"""
    bucketname = 'demo'
    resp = requests.post(BASE_URL + '/' + bucketname + '?create')
    assert resp.status_code == STATUS_OK

def test_list_object_in_bucket_exist_name():
    """GET /{bucketname}?list should have status_code 200"""
    bucketname = 'demo'
    resp = requests.get(BASE_URL + '/' + bucketname + '?list')
    assert resp.status_code == STATUS_OK

def test_list_object_in_bucket_not_exist_name():
    """GET /{bucketname}?list should have status_code 400"""
    bucketname = 'abc'
    resp = requests.get(BASE_URL + '/' + bucketname + '?list')
    assert resp.status_code == STATUS_BAD_REQUEST

def test_create_upload_ticket_exist_bucket_and_not_exist_object():
    """POST /{bucketname}/{objectname}?create should have status_code 200"""
    bucketname = 'demo'
    objectname = 'test.txt'
    resp = requests.post(BASE_URL + '/' + bucketname + '/' + objectname + '?create')
    assert resp.status_code == STATUS_OK

def test_create_upload_ticket_not_exist_bucket():
    """POST /{bucketname}/{objectname}?create should have status_code 400"""
    bucketname = 'abc'
    objectname = 'test.txt'
    resp = requests.post(BASE_URL + '/' + bucketname + '/' + objectname + '?create')
    assert resp.status_code == STATUS_BAD_REQUEST

# def test_upload_part():
#     """PUT /{bucketname}/{objectname}?partNumber=1 should have status_code 200"""
#     bucketname = 'demo'
#     objectname = 'test.txt'
#     partNumber = 1
#     headers = {'Content-Type': 'application/octet-stream', 'Content-Length': 16, 'Content-MD5': 'a431efdb90680b8d31d6d5574b02f922'
#     files = {'file': open('~/Desktop/test.txt', 'rb')}
#     url = BASE_URL + '/' + bucketname + '/' + objectname + '?partNumber=' + partNumber
#     resp = requests.put(url, files=files, headers=headers)
#     assert resp.status_code == STATUS_OK
#
# def test_upload_invalid_md5():
#     """PUT /{bucketname}/{objectname}?partNumber=1 should have status_code 400"""
#     bucketname = 'demo'
#     objectname = 'test.txt'
#     partNumber = 1
#     headers = {'Content-Type': 'application/octet-stream', 'Content-Length': 16, 'Content-MD5': 'a431efdb90680b8d31d6d5574b02f92'
#     files = {'file': open('~/Desktop/test.txt', 'rb')}
#     url = BASE_URL + '/' + bucketname + '/' + objectname + '?partNumber=' + partNumber
#     resp = requests.put(url, files=files, headers=headers)
#     assert resp.status_code == STATUS_BAD_REQUEST
#
# def test_upload_invalid_part_number():
#     """PUT /{bucketname}/{objectname}?partNumber=0 should have status_code 400"""
#     bucketname = 'demo'
#     objectname = 'test.txt'
#     partNumber = 0
#     headers = {'Content-Type': 'application/octet-stream', 'Content-Length': 16, 'Content-MD5': 'a431efdb90680b8d31d6d5574b02f92'
#     files = {'file': open('~/Desktop/test.txt', 'rb')}
#     url = BASE_URL + '/' + bucketname + '/' + objectname + '?partNumber=' + partNumber
#     resp = requests.put(url, files=files, headers=headers)
#     assert resp.status_code == STATUS_BAD_REQUEST
#
# def test_upload_invalid_object_name():
#     """PUT /{bucketname}/{objectname}?partNumber=0 should have status_code 400"""
#     bucketname = 'demo'
#     objectname = 'test.txt..'
#     partNumber = 0
#     headers = {'Content-Type': 'application/octet-stream', 'Content-Length': 16, 'Content-MD5': 'a431efdb90680b8d31d6d5574b02f92'
#     files = {'file': open('~/Desktop/test.txt', 'rb')}
#     url = BASE_URL + '/' + bucketname + '/' + objectname + '?partNumber=' + partNumber
#     resp = requests.put(url, files=files, headers=headers)
#     assert resp.status_code == STATUS_BAD_REQUEST

# def test_delete_part():
#     """Delete /{bucketname}/{objectname}?partNumber=1 should have status_code 200"""
#     bucketname = 'demo'
#     objectname = 'test.txt'
#     partNumber = 1
#     url = BASE_URL + '/' + bucketname + '/' + objectname + '?partNumber=' + partNumber
#     resp = requests.delete(url)
#     assert resp.status_code == STATUS_OK
#
# def test_delete_part_invalid_bucket_name():
#     """Delete /{bucketname}/{objectname}?partNumber=1 should have status_code 400"""
#     bucketname = 'demo.'
#     objectname = 'test.txt'
#     partNumber = 1
#     url = BASE_URL + '/' + bucketname + '/' + objectname + '?partNumber=' + partNumber
#     resp = requests.delete(url)
#     assert resp.status_code == STATUS_BAD_REQUEST

def test_complete_upload():
    """POST /{bucketname}/{objectname}?complete should have status_code 200"""
    bucketname = 'demo'
    objectname = 'test.txt'
    url = BASE_URL + '/' + bucketname + '/' + objectname + '?complete'
    resp = requests.post(url)
    assert resp.status_code == STATUS_OK

def test_complete_upload_invalid_bucket_name():
    """POST /{bucketname}/{objectname}?complete should have status_code 400"""
    bucketname = 'demo.'
    objectname = 'test.txt'
    url = BASE_URL + '/' + bucketname + '/' + objectname + '?complete'
    resp = requests.post(url)
    assert resp.status_code == STATUS_BAD_REQUEST

def test_complete_upload_invalid_bucket_name():
    """POST /{bucketname}/{objectname}?complete should have status_code 400"""
    bucketname = 'demo'
    objectname = 'test.txt..'
    url = BASE_URL + '/' + bucketname + '/' + objectname + '?complete'
    resp = requests.post(url)
    assert resp.status_code == STATUS_BAD_REQUEST
