import requests

BASE_URL = 'http://127.0.0.1:8080'
STATUS_OK = requests.codes['ok']
STATUS_BAD_REQUEST = requests.code['']

def test_get_status():
    """GET /status should have status_code 200"""
    resp = requests.get(BASE_URL + '/status')
    assert resp.status_code == STATUS_OK
