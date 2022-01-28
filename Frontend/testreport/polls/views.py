from django.http import HttpResponse
from .models import TestCampaign


def index(request):
    q = TestCampaign.objects.all()
    str_to_ret = ""
    for t in q:
        str_to_ret = str_to_ret + "\n" + t.__str__()
    return HttpResponse(str_to_ret)
