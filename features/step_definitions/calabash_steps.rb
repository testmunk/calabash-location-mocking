require 'calabash-android/calabash_steps'

Then /^I set my location to ([-+]?[0-9]*\.?[0-9]+), ([-+]?[0-9]*\.?[0-9]+)$/ do |latitude, longitude|
  set_gps_coordinates(latitude, longitude)
end
