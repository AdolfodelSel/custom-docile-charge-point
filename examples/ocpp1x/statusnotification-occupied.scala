statusNotification(status = ChargePointStatus.Occupied(Some(OccupancyKind.Preparing)))
statusNotification(status = ChargePointStatus.Occupied(Some(OccupancyKind.SuspendedEV)))

sleep(5.seconds)