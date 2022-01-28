from django.db import models


class TestCampaign(models.Model):
    id = models.IntegerField(primary_key=True)
    name = models.CharField(max_length=40)
    status = models.CharField(max_length=10)

    def __str__(self):
        return "Test Campaign: " + self.name.__str__() + " with status " + self.status.__str__()

